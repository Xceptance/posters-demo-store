package com.xceptance.posters.service;
import com.xceptance.posters.entity.Site;
import com.xceptance.posters.entity.Price;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.entity.CartLineItem;
import com.xceptance.posters.entity.CatalogCart;
import com.xceptance.posters.repository.CatalogCartRepository;
import com.xceptance.posters.repository.CatalogProductRepository;
import com.xceptance.posters.dto.CartDto;
import com.xceptance.posters.dto.CartItemDto;
import com.xceptance.posters.entity.Product;
import com.xceptance.posters.entity.Variant;
import com.xceptance.posters.service.LocalizedTextService;

@Service
public class CartService {

    private final CatalogCartRepository cartRepository;
    private final CatalogProductRepository catalogProductRepository;
    private final PostersProperties props;
    private final LocalizedTextService localizedTextService;

    @PersistenceContext
    private EntityManager em;

    public CartService(final CatalogCartRepository cartRepository, 
                       final CatalogProductRepository catalogProductRepository, 
                       final PostersProperties props, 
                       final LocalizedTextService localizedTextService) 
    {
        this.cartRepository = cartRepository;
        this.catalogProductRepository = catalogProductRepository;
        this.props = props;
        this.localizedTextService = localizedTextService;
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
        
        final BigDecimal currentPrice = lookupPrice(variantSku, currency);
        final String productName = localizedTextService.getText(product.getNameTextId(), "en-US");

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + qtyToAdd);
            existing.setUnitPrice(currentPrice); // update price to latest when modifying cart
            existing.setProductName(productName);
        } else {
            final CartLineItem li = new CartLineItem();
            li.setSku(variantSku);
            li.setQuantity(qtyToAdd);
            li.setUnitPrice(currentPrice);
            li.setProductName(productName);
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
            final BigDecimal unitPrice = li.getUnitPrice() != null ? li.getUnitPrice() : lookupPrice(li.getSku(), currency);
            subTotal = subTotal.add(unitPrice.multiply(BigDecimal.valueOf(li.getQuantity())));
        }
        cart.setSubTotal(subTotal);

        final BigDecimal shipping = cart.getShippingCosts() != null ? cart.getShippingCosts() : props.getShippingCosts();
        cart.setShippingCosts(shipping);

        final BigDecimal taxRate = cart.getTaxRate() != null ? cart.getTaxRate() : props.getTax();
        final BigDecimal tax = subTotal.add(shipping).multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        cart.setTotalTax(tax);

        cart.setTotal(subTotal.add(shipping).add(tax));
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

                if (attrName.contains("finish") && finish != null && attrValue.equalsIgnoreCase(finish.trim())) {
                    finishMatch = true;
                }
                if (attrName.contains("size") && size != null) {
                    // AI Agents often supply size parameters with trailing metric/imperial measurement units 
                    // (e.g. "24x18 in") sourced from the formatted distinctSizes labels. We aggressively strip 
                    // out ' in', ' cm', and inner spacing variations to securely normalize equality against 
                    // the raw DB dimensional constraints (e.g. "24x18").
                    final String normalizedInput = size.toLowerCase().replace(" in", "").replace(" cm", "").replace(" x ", "x").trim();
                    final String normalizedDb = attrValue.toLowerCase().replace(" in", "").replace(" cm", "").replace(" x ", "x").trim();
                    if (normalizedDb.equalsIgnoreCase(normalizedInput)) {
                        sizeMatch = true;
                    }
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

    /**
     * Converts a raw CatalogCart entity into a structured CartDto mapped for front-end templates.
     * Extracts dynamic size and finish strings natively for consistent presentation in checkout flows.
     * 
     * @param cart     The active shopping cart to convert.
     * @param locale   The current user's localization descriptor (e.g., "en-US").
     * @param currency The system currency utilized for calculation checks.
     * @return Formatted CartDto representing the order payload properties seamlessly.
     */
    public CartDto toCartDto(final CatalogCart cart, final String locale, final String currency) 
    {
        final List<CartItemDto> items = new ArrayList<>();
        int totalQty = 0;

        for (final CartLineItem li : cart.getLineItems()) 
        {
            final CartItemDto item = toCartItemDto(li, locale, currency);
            items.add(item);
            totalQty += li.getQuantity();
        }

        final BigDecimal taxRate = cart.getTaxRate() != null ? cart.getTaxRate() : props.getTax();
        final String taxStr = taxRate.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).toPlainString();

        return new CartDto(
            items,
            cart.getSubTotal() != null ? cart.getSubTotal() : BigDecimal.ZERO,
            cart.getTotalTax() != null ? cart.getTotalTax() : BigDecimal.ZERO,
            cart.getTotal() != null ? cart.getTotal() : BigDecimal.ZERO,
            cart.getShippingCosts() != null ? cart.getShippingCosts() : props.getShippingCosts(),
            taxStr,
            totalQty
        );
    }

    /**
     * Coverts a single CartLineItem database segment into a presentation-layer DTO wrapper.
     * Specifically executes product lookups to fetch explicit variant metadata not stored in raw carts.
     * 
     * @param li       The target cart line item to transpose.
     * @param locale   Environment locale map.
     * @param currency System pricing context.
     * @return Generated CartItemDto enriched with the required UI elements like product image URIs.
     */
    private CartItemDto toCartItemDto(final CartLineItem li, final String locale, final String currency) 
    {
        final String sku = li.getSku();
        final BigDecimal unitPrice = li.getUnitPrice() != null ? li.getUnitPrice() : lookupPrice(sku, currency);

        final String productSku = sku.contains("-") ? sku.substring(0, sku.lastIndexOf('-')) : sku;

        final Product product = catalogProductRepository.findBySku(productSku).orElse(null);
        String name = li.getProductName() != null ? li.getProductName() : "Unknown Product";
        String imageURL = "/images/placeholder.jpg";
        int productId = 0;
        String finish = "";
        String sizeLabel = "";

        if (product != null) 
        {
            productId = product.getId();
            name = li.getProductName() != null ? li.getProductName() : localizedTextService.getText(product.getNameTextId(), locale);
            imageURL = product.getMediumImageUrl();

            for (final Variant v : product.getVariants()) 
            {
                if (v.getFullSku().equals(sku)) 
                {
                    for (final var av : v.getAttributeValues()) 
                    {
                        final String attrName = av.getAttribute().getName().toLowerCase();
                        if (attrName.contains("finish")) 
                        {
                            finish = av.getValue();
                        } 
                        else if (attrName.contains("size")) 
                        {
                            sizeLabel = av.getValue();
                        }
                    }
                    break;
                }
            }
        }

        return new CartItemDto(
            li.getId() != null ? li.getId() : 0,
            productId,
            name,
            imageURL,
            finish,
            sizeLabel,
            unitPrice,
            li.getQuantity(),
            unitPrice.multiply(BigDecimal.valueOf(li.getQuantity())),
            sku
        );
    }

    /**
     * Retrieves the mapped system currency for a given user locale sequence.
     * 
     * @param locale the regional identifier mapped bounds (e.g., "en-US")
     * @return 3-letter currency code, defaults to "USD"
     */
    public String getCurrencyForLocale(final String locale) 
    {
        try 
        {
            final List<String> results = em.createQuery(
                "SELECT s.currency FROM Site s WHERE s.mainLocale.locale = :locale", String.class)
                .setParameter("locale", locale)
                .getResultList();
            if (!results.isEmpty()) return results.get(0);
        } 
        catch (final Exception e) 
        {
            // ignore explicit throws gracefully defaulting
        }
        
        if (locale.startsWith("de")) return "EUR";
        if (locale.equals("en-GB")) return "GBP";
        if (locale.equals("sv-SE")) return "SEK";
        return "USD";
    }
}
