package com.xceptance.posters.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.entity.CartLineItem;
import com.xceptance.posters.entity.CatalogCart;
import com.xceptance.posters.entity.CatalogCartRepository;
import com.xceptance.posters.entity.Product;
import com.xceptance.posters.entity.Variant;

@Service
public class CartService {

    private final CatalogCartRepository cartRepository;
    private final PostersProperties props;

    @PersistenceContext
    private EntityManager em;

    public CartService(final CatalogCartRepository cartRepository, final PostersProperties props) {
        this.cartRepository = cartRepository;
        this.props = props;
    }

    /**
     * Adds a product variant to the given cart and recalculates totals.
     * Enforces a maximum quantity limit of 99 items per addition attempt.
     * Also applies a safety length limit to finish and size strings to prevent ReDOS or memory bloat.
     * 
     * @param cart The user's current shopping cart.
     * @param product The product being added.
     * @param finish The requested finish string (e.g., "matte").
     * @param size The requested size string (e.g., "16 x 12 in").
     * @param requestedQuantity The amount of units requested to add.
     * @param currency The currency to calculate pricing in.
     * @return true if a valid variant was found and added, false otherwise (e.g., product has no purchasable variations).
     */
    @Transactional
    public boolean addProductToCart(final CatalogCart cart, final Product product, final String finish, final String size, final int requestedQuantity, final String currency) {
        
        final String safeSize = size != null && size.length() <= 50 ? size : null;
        final String safeFinish = finish != null && finish.length() <= 50 ? finish : null;

        Variant matchedVariant = null;
        if (safeSize != null || safeFinish != null) {
            matchedVariant = findVariant(product, safeFinish, safeSize);
        }

        if (matchedVariant == null) {
            matchedVariant = product.getVariants().isEmpty() ? null : product.getVariants().get(0);
        }

        if (matchedVariant == null) {
            return false; // Product has no purchasable variations
        }

        final String variantSku = matchedVariant.getFullSku();
        int qtyToAdd = requestedQuantity > 0 ? requestedQuantity : 1;
        
        if (qtyToAdd > 99) {
            qtyToAdd = 99;
        }

        CartLineItem existing = null;
        for (final CartLineItem li : cart.getLineItems()) {
            if (li.getSku().equals(variantSku)) {
                existing = li;
                break;
            }
        }

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + qtyToAdd);
        } else {
            final CartLineItem li = new CartLineItem();
            li.setSku(variantSku);
            li.setQuantity(qtyToAdd);
            cart.addLineItem(li);
        }

        recalculateTotals(cart, currency);
        cartRepository.save(cart);
        
        return true;
    }

    /**
     * Recalculates the subtotal, tax portions, and grand total of the cart based on current line items.
     * 
     * @param cart The user's current shopping cart to update.
     * @param currency The currency in which to evaluate item prices.
     */
    public void recalculateTotals(final CatalogCart cart, final String currency) {
        BigDecimal subTotal = BigDecimal.ZERO;
        for (final CartLineItem li : cart.getLineItems()) {
            final BigDecimal unitPrice = lookupPrice(li.getSku(), currency);
            subTotal = subTotal.add(unitPrice.multiply(BigDecimal.valueOf(li.getQuantity())));
        }
        cart.setSubTotal(subTotal);

        final BigDecimal taxRate = cart.getTaxRate() != null ? cart.getTaxRate() : props.getTax();
        final BigDecimal tax = subTotal.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        cart.setTotalTax(tax);

        final BigDecimal shipping = cart.getShippingCosts() != null ? cart.getShippingCosts() : props.getShippingCosts();
        cart.setTotal(subTotal.add(tax).add(shipping));
    }

    /**
     * Finds the exact variant configuration based on size and finish requested.
     * Falls back to matching by size only if the product lacks a finish attribute,
     * or defaults to the first available variant if multiple items match or none are uniquely identifiable by the given inputs.
     * 
     * @param product The base product to search within.
     * @param finish The finish requested.
     * @param size The size requested.
     * @return The specific matching Variant, or null if no variants exist.
     */
    public Variant findVariant(final Product product, final String finish, final String size) {
        if (product.getVariants() == null) return null;

        for (final Variant v : product.getVariants()) {
            boolean finishMatch = false;
            boolean sizeMatch = false;

            for (final var av : v.getAttributeValues()) {
                final String attrName = av.getAttribute().getName().toLowerCase();
                final String attrValue = av.getValue().toLowerCase();

                if (attrName.contains("finish") && finish != null && attrValue.equalsIgnoreCase(finish)) {
                    finishMatch = true;
                }
                if (attrName.contains("size") && size != null && attrValue.equalsIgnoreCase(size)) {
                    sizeMatch = true;
                }
            }

            if (finishMatch && sizeMatch) return v;

            // If product has no finish attribute, match by size only
            if (sizeMatch && !hasAttribute(product, "finish")) return v;
        }

        return product.getVariants().isEmpty() ? null : product.getVariants().get(0);
    }

    /**
     * Checks if a product has a given variation attribute category (e.g., "size" or "finish").
     * 
     * @param product The specific product to check.
     * @param name The lowercase target attribute substring.
     * @return true if the attribute exists on this product, false otherwise.
     */
    private boolean hasAttribute(final Product product, final String name) {
        if (product.getVariationAttributes() == null) return false;
        return product.getVariationAttributes().stream()
            .anyMatch(va -> va.getName().toLowerCase().contains(name));
    }

    /**
     * Resolves the price of a specific variant SKU in the given currency.
     * Queries the database for active pricing entries.
     * 
     * @param sku The full SKU of a variant (e.g., PRD-0001-0001).
     * @param currency The active currency (e.g., USD, EUR).
     * @return The resolved BigDecimal price or ZERO if not found.
     */
    public BigDecimal lookupPrice(final String sku, final String currency) {
        try {
            final List<BigDecimal> prices = em.createQuery(
                "SELECT p.price FROM Price p WHERE p.sku = :sku " +
                "AND p.priceTable.id IN (SELECT s.priceTable.id FROM Site s WHERE s.currency = :currency)",
                BigDecimal.class)
                .setParameter("sku", sku)
                .setParameter("currency", currency)
                .getResultList();
            if (!prices.isEmpty()) {
                return prices.get(0);
            }
        } catch (final Exception e) {
            // ignore
        }
        return BigDecimal.ZERO;
    }
}
