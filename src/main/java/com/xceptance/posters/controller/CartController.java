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
import com.xceptance.posters.service.CartService;
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
    private final CartService cartService;

    @PersistenceContext
    private EntityManager em;

    public CartController(final CatalogCartRepository cartRepository,
                          final CatalogProductRepository catalogProductRepository,
                          final LocalizedTextService textService,
                          final SessionService sessionService,
                          final PostersProperties props,
                          final CartService cartService)
    {
        this.cartRepository = cartRepository;
        this.catalogProductRepository = catalogProductRepository;
        this.textService = textService;
        this.sessionService = sessionService;
        this.props = props;
        this.cartService = cartService;
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
    public String viewCart(@PathVariable("locale") final String locale, final HttpSession session, final Model model)
    {
        final CatalogCart cart = sessionService.getCart(session);
        final String currency = getCurrencyForLocale(locale);
        model.addAttribute("cart", toCartDto(cart, locale, currency));
        return "cart/cart";
    }

    // ─── Add to Cart (WebMCP / JSON API) ───────────────────────────────

    /**
     * Dedicated JSON endpoint for the AI agent (WebMCP) to add items to the cart securely.
     * This endpoint handles finding the requested product variant based on size and finish,
     * enforcing safety limits (e.g. max quantity of 99), and updating the cart totals.
     * 
     * @param payload The AddToCartRequestDto containing productId, quantity, finish, and size.
     * @param session The current HTTP session to look up the active CatalogCart.
     * @param locale  The locale string used to format localized response names and lookup pricing.
     * @return AddToCartResponseDto indicating success state, detailed message, and total cart count.
     */
    @PostMapping(value = "/api/v2/cart/add", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public AddToCartResponseDto addJsonToCart(@RequestBody final AddToCartRequestDto payload,
                                              final HttpSession session,
                                              @RequestParam(value = "locale", required = false, defaultValue = "en-US") final String locale)
    {
        final CatalogCart cart = sessionService.getCart(session);
        final String currency = getCurrencyForLocale(locale);

        final Product product = catalogProductRepository.findById(payload.productId()).orElse(null);
        if (product == null)
        {
            final int currentQty = cart.getLineItems().stream().mapToInt(CartLineItem::getQuantity).sum();
            return new AddToCartResponseDto(false, "Failed to add to cart: Product not found.", currentQty);
        }

        final int requestedQuantity = payload.quantity() != null && payload.quantity() > 0 ? payload.quantity() : 1;
        final boolean success = cartService.addProductToCart(cart, product, payload.finish(), payload.size(), requestedQuantity, currency);

        if (!success)
        {
            final int currentQty = cart.getLineItems().stream().mapToInt(CartLineItem::getQuantity).sum();
            return new AddToCartResponseDto(false, "Failed to add to cart: Product has no purchasable variations.", currentQty);
        }

        final int newTotal = cart.getLineItems().stream().mapToInt(CartLineItem::getQuantity).sum();
        final String name = textService.getText(product.getNameTextId(), locale);
        return new AddToCartResponseDto(true, "Successfully added " + name + " to the shopping cart.", newTotal);
    }

    // ─── Add to Cart (HTMX) ────────────────────────────────────────────

    @GetMapping("/{locale}/addToCartSlider")
    public String addToCartSlider(@PathVariable("locale") final String locale,
                                  @RequestParam("productId") final int productId,
                                  @RequestParam("finish") final String finish,
                                  @RequestParam("size") final String size,
                                  final HttpSession session,
                                  final Model model)
    {
        final CatalogCart cart = sessionService.getCart(session);
        final String currency = getCurrencyForLocale(locale);

        final Product product = catalogProductRepository.findById(productId).orElse(null);
        if (product != null)
        {
            cartService.addProductToCart(cart, product, finish, size, 1, currency);
        }

        return populateMiniCartModel(locale, cart, currency, model);
    }

    // ─── Mini Cart (HTMX) ───────────────────────────────────────────────

    @GetMapping("/{locale}/miniCart")
    public String miniCart(@PathVariable("locale") final String locale, final HttpSession session, final Model model)
    {
        final CatalogCart cart = sessionService.getCart(session);
        final String currency = getCurrencyForLocale(locale);
        return populateMiniCartModel(locale, cart, currency, model);
    }

    // ─── Update Quantity (HTMX) ─────────────────────────────────────────

    @PostMapping("/{locale}/updateProductCount")
    public String updateProductCount(@PathVariable("locale") final String locale,
                                     @RequestParam("cartProductId") final int cartProductId,
                                     @RequestParam("productCount") final int productCount,
                                     final HttpSession session,
                                     final Model model)
    {
        final CatalogCart cart = sessionService.getCart(session);
        final String currency = getCurrencyForLocale(locale);

        for (final CartLineItem li : cart.getLineItems())
        {
            if (li.getId() != null && li.getId() == cartProductId)
            {
                li.setQuantity(productCount);
                break;
            }
        }
        cartService.recalculateTotals(cart, currency);
        cartRepository.save(cart);

        return populateCartBodyModel(locale, cart, currency, model);
    }

    // ─── Delete from Cart (HTMX) ────────────────────────────────────────

    @PostMapping("/{locale}/deleteFromCart")
    public String deleteFromCart(@PathVariable("locale") final String locale,
                                @RequestParam("cartProductId") final int cartProductId,
                                final HttpSession session,
                                final Model model)
    {
        final CatalogCart cart = sessionService.getCart(session);
        final String currency = getCurrencyForLocale(locale);

        cart.getLineItems().removeIf(li -> li.getId() != null && li.getId() == cartProductId);
        cartService.recalculateTotals(cart, currency);
        cartRepository.save(cart);

        return populateCartBodyModel(locale, cart, currency, model);
    }

    // ─── Update Price (HTMX - product detail page) ──────────────────────

    @PostMapping("/{locale}/updatePrice")
    public String updatePrice(@PathVariable("locale") final String locale,
                              @RequestParam("productId") final int productId,
                              @RequestParam("size") final String size,
                              @RequestParam(value = "finish", required = false, defaultValue = "matte") final String finish,
                              final Model model)
    {
        final String currency = getCurrencyForLocale(locale);
        String formattedPrice = "$0.00";

        final Product product = catalogProductRepository.findById(productId).orElse(null);
        if (product != null)
        {
            final Variant variant = cartService.findVariant(product, finish, size);
            if (variant != null)
            {
                final BigDecimal price = cartService.lookupPrice(variant.getFullSku(), currency);
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
    /**
     * Convert a CatalogCart + its line items into a CartDto for template rendering.
     */
    private CartDto toCartDto(final CatalogCart cart, final String locale, final String currency)
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
        final String taxStr = taxRate.stripTrailingZeros().toPlainString();

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
    private CartItemDto toCartItemDto(final CartLineItem li, final String locale, final String currency)
    {
        final String sku = li.getSku();
        final BigDecimal unitPrice = cartService.lookupPrice(sku, currency);

        // Derive product SKU from variant SKU: "PRD-0001-0001" → "PRD-0001"
        final String productSku = sku.contains("-") ? sku.substring(0, sku.lastIndexOf('-')) : sku;

        // Look up product
        final Product product = catalogProductRepository.findBySku(productSku).orElse(null);
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

    private String populateMiniCartModel(final String locale, final CatalogCart cart, final String currency, final Model model)
    {
        final CartDto cartDto = toCartDto(cart, locale, currency);
        model.addAttribute("cartProducts", cartDto.products());
        model.addAttribute("cartProductCount", cartDto.productCount());
        model.addAttribute("subTotalPrice", cartDto.subTotalPrice());
        model.addAttribute("unitLength", unitLengthForLocale(locale));
        return "fragments/miniCartFragment";
    }

    private String populateCartBodyModel(final String locale, final CatalogCart cart, final String currency, final Model model)
    {
        final CartDto cartDto = toCartDto(cart, locale, currency);
        model.addAttribute("cart", cartDto);
        return "fragments/cartBodyFragment";
    }

    private String unitLengthForLocale(final String locale)
    {
        if (locale.startsWith("de") || locale.equals("en-GB") || locale.equals("sv-SE")) return "cm";
        return props.getUnitOfLength();
    }

    private String getCurrencyForLocale(final String locale)
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
            // ignore
        }
        if (locale.startsWith("de")) return "EUR";
        if (locale.equals("en-GB")) return "GBP";
        if (locale.equals("sv-SE")) return "SEK";
        return "USD";
    }
}
