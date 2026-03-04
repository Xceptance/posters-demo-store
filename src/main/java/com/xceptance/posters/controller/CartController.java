package com.xceptance.posters.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.xceptance.posters.util.PriceFormatter;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.model.Cart;
import com.xceptance.posters.model.CartProduct;
import com.xceptance.posters.model.PosterSize;
import com.xceptance.posters.model.Product;
import com.xceptance.posters.model.ProductPosterSize;
import com.xceptance.posters.repository.CartProductRepository;
import com.xceptance.posters.repository.CartRepository;
import com.xceptance.posters.repository.PosterSizeRepository;
import com.xceptance.posters.repository.ProductPosterSizeRepository;
import com.xceptance.posters.repository.ProductRepository;
import com.xceptance.posters.service.SessionService;

import jakarta.servlet.http.HttpSession;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles shopping cart operations: add, update, remove products and view cart.
 *
 * All AJAX-style endpoints (mini-cart, add-to-cart, update, delete) return
 * Thymeleaf HTML fragments for use with HTMX instead of JSON.
 */
@Controller
public class CartController
{
    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ProductRepository productRepository;
    private final PosterSizeRepository posterSizeRepository;
    private final ProductPosterSizeRepository productPosterSizeRepository;
    private final SessionService sessionService;
    private final PostersProperties props;

    public CartController(CartRepository cartRepository,
                          CartProductRepository cartProductRepository,
                          ProductRepository productRepository,
                          PosterSizeRepository posterSizeRepository,
                          ProductPosterSizeRepository productPosterSizeRepository,
                          SessionService sessionService,
                          PostersProperties props)
    {
        this.cartRepository = cartRepository;
        this.cartProductRepository = cartProductRepository;
        this.productRepository = productRepository;
        this.posterSizeRepository = posterSizeRepository;
        this.productPosterSizeRepository = productPosterSizeRepository;
        this.sessionService = sessionService;
        this.props = props;
    }

    @GetMapping("/{locale}/cart")
    public String viewCart(@PathVariable("locale") String locale, HttpSession session, Model model)
    {
        Cart cart = sessionService.getCart(session);
        model.addAttribute("cart", cart);
        return "cart/cart";
    }

    /**
     * Add to cart via HTMX. Accepts size as a string like "16 x 12 in",
     * parses width/height, adds the product to the cart, and returns the
     * mini-cart HTML fragment (with OOB header count update).
     */
    @GetMapping("/{locale}/addToCartSlider")
    public String addToCartSlider(@PathVariable("locale") String locale,
                                  @RequestParam("productId") int productId,
                                  @RequestParam("finish") String finish,
                                  @RequestParam("size") String size,
                                  HttpSession session,
                                  Model model)
    {
        Cart cart = sessionService.getCart(session);
        Product product = productRepository.findById(productId).orElse(null);

        if (product != null)
        {
            // Parse size string like "16 x 12 in" to extract width and height
            Pattern sizePattern = Pattern.compile("(\\d+)\\s*x\\s*(\\d+)");
            Matcher matcher = sizePattern.matcher(size);
            int width = 0, height = 0;
            if (matcher.find())
            {
                width = Integer.parseInt(matcher.group(1));
                height = Integer.parseInt(matcher.group(2));
            }

            PosterSize posterSize = posterSizeRepository.findByWidthAndHeight(width, height);
            if (posterSize != null)
            {
                // Get price (locale-aware)
                ProductPosterSize pps = productPosterSizeRepository.findByProductAndSize(product, posterSize);
                double price = pps != null ? pps.getPrice(locale) : 0;

                // Check if item already in cart
                CartProduct existing = cartProductRepository.findByCartAndProductAndFinishAndSize(cart, product, finish, posterSize);
                if (existing != null)
                {
                    existing.incProductCount();
                    cartProductRepository.save(existing);
                }
                else
                {
                    CartProduct cp = new CartProduct();
                    cp.setCart(cart);
                    cp.setProduct(product);
                    cp.setFinish(finish);
                    cp.setSize(posterSize);
                    cp.setProductCount(1);
                    cp.setPrice(price);
                    cartProductRepository.save(cp);
                    cart.getProducts().add(cp);
                }

                // Recalculate cart totals
                cart.setSubTotalPrice(cart.getSubTotalPrice() + price);
                cart.calculateTotalTaxPrice();
                cart.calculateTotalPrice();
                cartRepository.save(cart);
            }
        }

        // Return mini-cart fragment (includes OOB header count update)
        return populateMiniCartModel(locale, cart, model);
    }

    /**
     * Get mini-cart content as an HTML fragment via HTMX.
     * Called when the mini-cart dropdown is opened.
     */
    @GetMapping("/{locale}/miniCart")
    public String miniCart(@PathVariable("locale") String locale, HttpSession session, Model model)
    {
        Cart cart = sessionService.getCart(session);
        return populateMiniCartModel(locale, cart, model);
    }

    /**
     * Update the quantity of a product in the cart.
     * Returns the cart body HTML fragment via HTMX.
     */
    @PostMapping("/{locale}/updateProductCount")
    public String updateProductCount(@PathVariable("locale") String locale,
                                     @RequestParam("cartProductId") int cartProductId,
                                     @RequestParam("productCount") int productCount,
                                     HttpSession session,
                                     Model model)
    {
        CartProduct cp = cartProductRepository.findById(cartProductId).orElse(null);
        Cart cart = sessionService.getCart(session);

        if (cp != null)
        {
            double priceDiff = cp.getPrice() * (productCount - cp.getProductCount());
            cp.setProductCount(productCount);
            cartProductRepository.save(cp);

            cart.setSubTotalPrice(cart.getSubTotalPrice() + priceDiff);
            cart.calculateTotalTaxPrice();
            cart.calculateTotalPrice();
            cartRepository.save(cart);
        }

        return populateCartBodyModel(locale, cart, model);
    }

    /**
     * Delete a product from the cart.
     * Returns the cart body HTML fragment via HTMX.
     */
    @PostMapping("/{locale}/deleteFromCart")
    public String deleteFromCart(@PathVariable("locale") String locale,
                                @RequestParam("cartProductId") int cartProductId,
                                HttpSession session,
                                Model model)
    {
        CartProduct cp = cartProductRepository.findById(cartProductId).orElse(null);
        Cart cart = sessionService.getCart(session);

        if (cp != null)
        {
            cart.setSubTotalPrice(cart.getSubTotalPrice() - (cp.getPrice() * cp.getProductCount()));
            cart.getProducts().remove(cp);
            cartProductRepository.delete(cp);
            cart.calculateTotalTaxPrice();
            cart.calculateTotalPrice();
            cartRepository.save(cart);
        }

        return populateCartBodyModel(locale, cart, model);
    }

    /**
     * Update the product price when the selected size changes.
     * Returns just a price span fragment via HTMX.
     */
    @PostMapping("/{locale}/updatePrice")
    public String updatePrice(@PathVariable("locale") String locale,
                              @RequestParam("productId") int productId,
                              @RequestParam("size") String size,
                              Model model)
    {
        Product product = productRepository.findById(productId).orElse(null);
        String formattedPrice = "$0.00";

        if (product != null)
        {
            // Parse size string like "16 x 12 in" to extract width and height
            Pattern sizePattern = Pattern.compile("(\\d+)\\s*x\\s*(\\d+)");
            Matcher matcher = sizePattern.matcher(size);
            int width = 0, height = 0;
            if (matcher.find())
            {
                width = Integer.parseInt(matcher.group(1));
                height = Integer.parseInt(matcher.group(2));
            }

            PosterSize posterSize = posterSizeRepository.findByWidthAndHeight(width, height);
            if (posterSize != null)
            {
                ProductPosterSize pps = productPosterSizeRepository.findByProductAndSize(product, posterSize);
                if (pps != null)
                {
                    formattedPrice = PriceFormatter.format(pps.getPrice(locale), locale);
                }
            }
        }

        model.addAttribute("formattedPrice", formattedPrice);
        return "fragments/priceFragment";
    }

    /**
     * Populate model for the mini-cart fragment and return the view name.
     */
    private String populateMiniCartModel(String locale, Cart cart, Model model)
    {
        model.addAttribute("cartProducts", cart.getProducts());
        model.addAttribute("cartProductCount", cart.getProductCount());
        model.addAttribute("subTotalPrice", cart.getSubTotalPrice());
        model.addAttribute("unitLength", unitLengthForLocale(locale));
        return "fragments/miniCartFragment";
    }

    /**
     * Populate model for the cart body fragment and return the view name.
     */
    private String populateCartBodyModel(String locale, Cart cart, Model model)
    {
        model.addAttribute("cart", cart);
        return "fragments/cartBodyFragment";
    }

    private String unitLengthForLocale(String locale)
    {
        if (locale.startsWith("de") || locale.equals("en-GB") || locale.equals("sv-SE")) return "cm";
        return props.getUnitOfLength();
    }
}
