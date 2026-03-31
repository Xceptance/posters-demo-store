package com.xceptance.posters.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.entity.CartLineItem;
import com.xceptance.posters.entity.CatalogCart;
import com.xceptance.posters.entity.CatalogCartRepository;
import com.xceptance.posters.entity.CatalogProductRepository;
import com.xceptance.posters.entity.LocalizedTextService;
import com.xceptance.posters.entity.Product;
import com.xceptance.posters.entity.Variant;
import com.xceptance.posters.service.SessionService;
import com.xceptance.posters.util.PriceFormatter;

/**
 * Handles shopping cart operations: add, update, remove products and view cart.
 * Uses the new entity model (CatalogCart, CartLineItem, Variant, Price).
 *
 * All AJAX-style endpoints return Thymeleaf HTML fragments for use with HTMX.
 */
@Controller
public class CartController
{
    private final CatalogCartRepository cartRepository;
    private final CatalogProductRepository catalogProductRepository;
    private final LocalizedTextService textService;
    private final SessionService sessionService;
    private final PostersProperties props;

    @PersistenceContext
    private EntityManager em;

    public CartController(CatalogCartRepository cartRepository,
                          CatalogProductRepository catalogProductRepository,
                          LocalizedTextService textService,
                          SessionService sessionService,
                          PostersProperties props)
    {
        this.cartRepository = cartRepository;
        this.catalogProductRepository = catalogProductRepository;
        this.textService = textService;
        this.sessionService = sessionService;
        this.props = props;
    }

    // ─── DTOs for JSON Agents ──────────────────────────────────────────

    public record AddToCartRequestDto(int productId, Integer quantity, String size, String finish) {}
    public record AddToCartResponseDto(boolean success, String message, int cartCount) {}

    // ─── DTOs for templates ────────────────────────────────────────────

    /**
     * DTO for a single cart item as rendered in the cart page and mini-cart.
     * Pre-resolves product name, image, variant details so templates just use fields.
     */
    public record CartItemDto(
        int lineItemId,
        int productId,
        String productName,
        String imageURL,
        String finish,
        String sizeLabel,
        BigDecimal price,
        int productCount,
        BigDecimal totalProductPrice,
        String sku
    ) {}

    /**
     * DTO wrapping the full cart for template rendering, providing the same
     * field names the legacy templates expected.
     */
    public record CartDto(
        List<CartItemDto> products,
        BigDecimal subTotalPrice,
        BigDecimal totalTaxPrice,
        BigDecimal totalPrice,
        BigDecimal shippingCosts,
        String taxAsString,
        int productCount
    ) {}

    // ─── View Cart ──────────────────────────────────────────────────────

    @GetMapping("/{locale}/cart")
    public String viewCart(@PathVariable("locale") String locale, HttpSession session, Model model)
    {
        CatalogCart cart = sessionService.getCart(session);
        String currency = getCurrencyForLocale(locale);
        model.addAttribute("cart", toCartDto(cart, locale, currency));
        return "cart/cart";
    }

    // ─── Add to Cart (WebMCP / JSON API) ───────────────────────────────

    /**
     * Dedicated JSON endpoint for the AI agent to add items to the cart securely.
     */
    @PostMapping(value = "/api/v2/cart/add", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public AddToCartResponseDto addJsonToCart(@RequestBody AddToCartRequestDto payload,
                                              HttpSession session,
                                              @RequestParam(value = "locale", required = false, defaultValue = "en-US") String locale)
    {
        // 1. SECURITY: Validate payload string length (prevent ReDOS or memory bloat)
        String safeSize = payload.size() != null && payload.size().length() <= 50 ? payload.size() : null;
        String safeFinish = payload.finish() != null && payload.finish().length() <= 50 ? payload.finish() : null;

        CatalogCart cart = sessionService.getCart(session);
        String currency = getCurrencyForLocale(locale);

        Product product = catalogProductRepository.findById(payload.productId()).orElse(null);
        if (product == null)
        {
            int currentQty = cart.getLineItems().stream().mapToInt(CartLineItem::getQuantity).sum();
            return new AddToCartResponseDto(false, "Failed to add to cart: Product not found.", currentQty);
        }

        // Attempt to find a specific variant if the AI provided size/finish preferences
        Variant matchedVariant = null;
        if (safeSize != null || safeFinish != null)
        {
            matchedVariant = findVariant(product, safeFinish, safeSize);
        }

        // Securely fallback to the first default variant if no preferences provided or if the exact combo doesn't exist
        if (matchedVariant == null)
        {
            matchedVariant = product.getVariants().isEmpty() ? null : product.getVariants().get(0);
        }

        if (matchedVariant == null)
        {
            int currentQty = cart.getLineItems().stream().mapToInt(CartLineItem::getQuantity).sum();
            return new AddToCartResponseDto(false, "Failed to add to cart: Product has no purchasable variations.", currentQty);
        }

        String variantSku = matchedVariant.getFullSku();
        int qtyToAdd = payload.quantity() != null && payload.quantity() > 0 ? payload.quantity() : 1;
        
        // 2. SECURITY: Cap maximum quantity to prevent DOS, OOM crashes, or database numeric overflow limit errors.
        if (qtyToAdd > 99) 
        {
            qtyToAdd = 99;
        }

        CartLineItem existing = null;
        for (CartLineItem li : cart.getLineItems())
        {
            if (li.getSku().equals(variantSku))
            {
                existing = li;
                break;
            }
        }

        if (existing != null)
        {
            existing.setQuantity(existing.getQuantity() + qtyToAdd);
        }
        else
        {
            CartLineItem li = new CartLineItem();
            li.setSku(variantSku);
            li.setQuantity(qtyToAdd);
            cart.addLineItem(li);
        }

        // Recalculate totals and hit the database
        recalculateTotals(cart, currency);
        cartRepository.save(cart);

        int newTotal = cart.getLineItems().stream().mapToInt(CartLineItem::getQuantity).sum();
        String name = textService.getText(product.getNameTextId(), locale);
        return new AddToCartResponseDto(true, "Successfully added " + name + " to the shopping cart.", newTotal);
    }

    // ─── Add to Cart (HTMX) ────────────────────────────────────────────

    @GetMapping("/{locale}/addToCartSlider")
    public String addToCartSlider(@PathVariable("locale") String locale,
                                  @RequestParam("productId") int productId,
                                  @RequestParam("finish") String finish,
                                  @RequestParam("size") String size,
                                  HttpSession session,
                                  Model model)
    {
        CatalogCart cart = sessionService.getCart(session);
        String currency = getCurrencyForLocale(locale);

        // Find the variant by product + attributes
        Product product = catalogProductRepository.findById(productId).orElse(null);
        if (product != null)
        {
            Variant matchedVariant = findVariant(product, finish, size);
            if (matchedVariant != null)
            {
                String variantSku = matchedVariant.getFullSku();
                BigDecimal unitPrice = lookupPrice(variantSku, currency);

                // Check if item already in cart
                CartLineItem existing = null;
                for (CartLineItem li : cart.getLineItems())
                {
                    if (li.getSku().equals(variantSku))
                    {
                        existing = li;
                        break;
                    }
                }

                if (existing != null)
                {
                    existing.setQuantity(existing.getQuantity() + 1);
                }
                else
                {
                    CartLineItem li = new CartLineItem();
                    li.setSku(variantSku);
                    li.setQuantity(1);
                    cart.addLineItem(li);
                }

                // Recalculate totals
                recalculateTotals(cart, currency);
                cartRepository.save(cart);
            }
        }

        return populateMiniCartModel(locale, cart, currency, model);
    }

    // ─── Mini Cart (HTMX) ───────────────────────────────────────────────

    @GetMapping("/{locale}/miniCart")
    public String miniCart(@PathVariable("locale") String locale, HttpSession session, Model model)
    {
        CatalogCart cart = sessionService.getCart(session);
        String currency = getCurrencyForLocale(locale);
        return populateMiniCartModel(locale, cart, currency, model);
    }

    // ─── Update Quantity (HTMX) ─────────────────────────────────────────

    @PostMapping("/{locale}/updateProductCount")
    public String updateProductCount(@PathVariable("locale") String locale,
                                     @RequestParam("cartProductId") int cartProductId,
                                     @RequestParam("productCount") int productCount,
                                     HttpSession session,
                                     Model model)
    {
        CatalogCart cart = sessionService.getCart(session);
        String currency = getCurrencyForLocale(locale);

        for (CartLineItem li : cart.getLineItems())
        {
            if (li.getId() != null && li.getId() == cartProductId)
            {
                li.setQuantity(productCount);
                break;
            }
        }
        recalculateTotals(cart, currency);
        cartRepository.save(cart);

        return populateCartBodyModel(locale, cart, currency, model);
    }

    // ─── Delete from Cart (HTMX) ────────────────────────────────────────

    @PostMapping("/{locale}/deleteFromCart")
    public String deleteFromCart(@PathVariable("locale") String locale,
                                @RequestParam("cartProductId") int cartProductId,
                                HttpSession session,
                                Model model)
    {
        CatalogCart cart = sessionService.getCart(session);
        String currency = getCurrencyForLocale(locale);

        cart.getLineItems().removeIf(li -> li.getId() != null && li.getId() == cartProductId);
        recalculateTotals(cart, currency);
        cartRepository.save(cart);

        return populateCartBodyModel(locale, cart, currency, model);
    }

    // ─── Update Price (HTMX - product detail page) ──────────────────────

    @PostMapping("/{locale}/updatePrice")
    public String updatePrice(@PathVariable("locale") String locale,
                              @RequestParam("productId") int productId,
                              @RequestParam("size") String size,
                              @RequestParam(value = "finish", required = false, defaultValue = "matte") String finish,
                              Model model)
    {
        String currency = getCurrencyForLocale(locale);
        String formattedPrice = "$0.00";

        Product product = catalogProductRepository.findById(productId).orElse(null);
        if (product != null)
        {
            Variant variant = findVariant(product, finish, size);
            if (variant != null)
            {
                BigDecimal price = lookupPrice(variant.getFullSku(), currency);
                formattedPrice = PriceFormatter.format(price, locale);
            }
        }

        model.addAttribute("formattedPrice", formattedPrice);
        return "fragments/priceFragment";
    }

    // ─── Helpers ────────────────────────────────────────────────────────

    /**
     * Find a variant matching the given finish and size on a product.
     * Size is a string like "16 x 12 in" and finish is a string like "matte".
     * We match by checking variant attribute values.
     */
    private Variant findVariant(Product product, String finish, String size)
    {
        if (product.getVariants() == null) return null;

        for (Variant v : product.getVariants())
        {
            boolean finishMatch = false;
            boolean sizeMatch = false;

            for (var av : v.getAttributeValues())
            {
                String attrName = av.getAttribute().getName().toLowerCase();
                String attrValue = av.getValue().toLowerCase();

                if (attrName.contains("finish") && attrValue.equalsIgnoreCase(finish))
                {
                    finishMatch = true;
                }
                if (attrName.contains("size") && attrValue.equalsIgnoreCase(size))
                {
                    sizeMatch = true;
                }
            }

            if (finishMatch && sizeMatch) return v;

            // If product has no finish attribute, match by size only
            if (sizeMatch && !hasAttribute(product, "finish")) return v;
        }

        // Fallback: return first variant
        return product.getVariants().isEmpty() ? null : product.getVariants().get(0);
    }

    private boolean hasAttribute(Product product, String name)
    {
        if (product.getVariationAttributes() == null) return false;
        return product.getVariationAttributes().stream()
            .anyMatch(va -> va.getName().toLowerCase().contains(name));
    }

    private BigDecimal lookupPrice(String sku, String currency)
    {
        try
        {
            List<BigDecimal> prices = em.createQuery(
                "SELECT p.price FROM Price p WHERE p.sku = :sku " +
                "AND p.priceTable.id IN (SELECT s.priceTable.id FROM Site s WHERE s.currency = :currency)",
                BigDecimal.class)
                .setParameter("sku", sku)
                .setParameter("currency", currency)
                .getResultList();
            if (!prices.isEmpty())
            {
                return prices.get(0);
            }
        }
        catch (Exception e)
        {
            // ignore
        }
        return BigDecimal.ZERO;
    }

    private void recalculateTotals(CatalogCart cart, String currency)
    {
        BigDecimal subTotal = BigDecimal.ZERO;
        for (CartLineItem li : cart.getLineItems())
        {
            BigDecimal unitPrice = lookupPrice(li.getSku(), currency);
            subTotal = subTotal.add(unitPrice.multiply(BigDecimal.valueOf(li.getQuantity())));
        }
        cart.setSubTotal(subTotal);

        BigDecimal taxRate = cart.getTaxRate() != null ? cart.getTaxRate() : props.getTax();
        BigDecimal tax = subTotal.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        cart.setTotalTax(tax);

        BigDecimal shipping = cart.getShippingCosts() != null ? cart.getShippingCosts() : props.getShippingCosts();
        cart.setTotal(subTotal.add(tax).add(shipping));
    }

    /**
     * Convert a CatalogCart + its line items into a CartDto for template rendering.
     */
    private CartDto toCartDto(CatalogCart cart, String locale, String currency)
    {
        List<CartItemDto> items = new ArrayList<>();
        int totalQty = 0;

        for (CartLineItem li : cart.getLineItems())
        {
            CartItemDto item = toCartItemDto(li, locale, currency);
            items.add(item);
            totalQty += li.getQuantity();
        }

        BigDecimal taxRate = cart.getTaxRate() != null ? cart.getTaxRate() : props.getTax();
        String taxStr = taxRate.stripTrailingZeros().toPlainString();

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
     * Convert a CartLineItem into a CartItemDto by resolving product/variant details.
     * The SKU format is "PRD-XXXX-YYYY" where PRD-XXXX is the product SKU.
     */
    private CartItemDto toCartItemDto(CartLineItem li, String locale, String currency)
    {
        String sku = li.getSku();
        BigDecimal unitPrice = lookupPrice(sku, currency);

        // Derive product SKU from variant SKU: "PRD-0001-0001" → "PRD-0001"
        String productSku = sku.contains("-") ? sku.substring(0, sku.lastIndexOf('-')) : sku;

        // Look up product
        Product product = catalogProductRepository.findBySku(productSku).orElse(null);
        String name = "Unknown Product";
        String imageURL = "/images/placeholder.jpg";
        int productId = 0;
        String finish = "";
        String sizeLabel = "";

        if (product != null)
        {
            productId = product.getId();
            name = textService.getText(product.getNameTextId(), locale);
            imageURL = product.getMediumImageUrl();

            // Find the variant to get attribute details
            for (Variant v : product.getVariants())
            {
                if (v.getFullSku().equals(sku))
                {
                    for (var av : v.getAttributeValues())
                    {
                        String attrName = av.getAttribute().getName().toLowerCase();
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

    private String populateMiniCartModel(String locale, CatalogCart cart, String currency, Model model)
    {
        CartDto cartDto = toCartDto(cart, locale, currency);
        model.addAttribute("cartProducts", cartDto.products());
        model.addAttribute("cartProductCount", cartDto.productCount());
        model.addAttribute("subTotalPrice", cartDto.subTotalPrice());
        model.addAttribute("unitLength", unitLengthForLocale(locale));
        return "fragments/miniCartFragment";
    }

    private String populateCartBodyModel(String locale, CatalogCart cart, String currency, Model model)
    {
        CartDto cartDto = toCartDto(cart, locale, currency);
        model.addAttribute("cart", cartDto);
        return "fragments/cartBodyFragment";
    }

    private String unitLengthForLocale(String locale)
    {
        if (locale.startsWith("de") || locale.equals("en-GB") || locale.equals("sv-SE")) return "cm";
        return props.getUnitOfLength();
    }

    private String getCurrencyForLocale(String locale)
    {
        try
        {
            List<String> results = em.createQuery(
                "SELECT s.currency FROM Site s WHERE s.mainLocale.locale = :locale", String.class)
                .setParameter("locale", locale)
                .getResultList();
            if (!results.isEmpty()) return results.get(0);
        }
        catch (Exception e)
        {
            // ignore
        }
        if (locale.startsWith("de")) return "EUR";
        if (locale.equals("en-GB")) return "GBP";
        if (locale.equals("sv-SE")) return "SEK";
        return "USD";
    }
}
