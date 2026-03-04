package com.xceptance.posters.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.util.PriceFormatter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handles shopping cart operations: add, update, remove products and view cart.
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
    public String viewCart(@PathVariable String locale, HttpSession session, Model model)
    {
        Cart cart = sessionService.getCart(session);
        model.addAttribute("cart", cart);
        return "cart/cart";
    }

    /**
     * Add to cart via AJAX - used by posterMiniCart.js.
     * Accepts size as a string like "16 x 12 in", parses width/height,
     * and returns JSON matching the JS expectations.
     */
    @GetMapping("/{locale}/addToCartSlider")
    @ResponseBody
    public Map<String, Object> addToCartSlider(@PathVariable String locale,
                                                @RequestParam int productId,
                                                @RequestParam String finish,
                                                @RequestParam String size,
                                                HttpSession session)
    {
        Cart cart = sessionService.getCart(session);
        Product product = productRepository.findById(productId).orElse(null);
        Map<String, Object> response = new HashMap<>();

        if (product == null)
        {
            response.put("success", false);
            return response;
        }

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
        if (posterSize == null)
        {
            response.put("success", false);
            return response;
        }

        // Get price (locale-aware)
        ProductPosterSize pps = productPosterSizeRepository.findByProductAndSize(product, posterSize);
        double price = pps != null ? pps.getPrice(locale) : 0;

        // Check if item already in cart
        CartProduct existing = cartProductRepository.findByCartAndProductAndFinishAndSize(cart, product, finish, posterSize);
        CartProduct cartProduct;
        if (existing != null)
        {
            existing.incProductCount();
            cartProductRepository.save(existing);
            cartProduct = existing;
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
            cartProduct = cp;
        }

        // Recalculate cart totals
        cart.setSubTotalPrice(cart.getSubTotalPrice() + price);
        cart.calculateTotalTaxPrice();
        cart.calculateTotalPrice();
        cartRepository.save(cart);

        // Build response matching posterMiniCart.js expectations
        Map<String, Object> productData = new HashMap<>();
        productData.put("localizedName", product.getName().getText(locale));
        productData.put("productCount", cartProduct.getProductCount());
        productData.put("finish", finish);
        productData.put("productTotalUnitPrice", PriceFormatter.format(cartProduct.getPrice() * cartProduct.getProductCount(), locale));

        Map<String, Object> sizeData = new HashMap<>();
        sizeData.put("width", posterSize.getWidth());
        sizeData.put("height", posterSize.getHeight());
        productData.put("size", sizeData);

        response.put("product", productData);
        response.put("unitLength", unitLengthForLocale(locale));
        response.put("subOrderTotal", PriceFormatter.format(cart.getSubTotalPrice(), locale));
        response.put("headerCartOverview", cart.getProductCount());
        return response;
    }

    /**
     * Get mini cart elements for the dropdown - used by posterMiniCart.js.
     */
    @GetMapping("/{locale}/getMiniCartElements")
    @ResponseBody
    public Map<String, Object> getMiniCartElements(@PathVariable String locale, HttpSession session)
    {
        Cart cart = sessionService.getCart(session);
        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> productsInCartList = new ArrayList<>();
        for (CartProduct cp : cart.getProducts())
        {
            Map<String, Object> productData = new HashMap<>();
            productData.put("localizedName", cp.getProduct().getName().getText(locale));
            productData.put("productCount", cp.getProductCount());
            productData.put("finish", cp.getFinish());
            productData.put("productTotalUnitPrice", PriceFormatter.format(cp.getPrice() * cp.getProductCount(), locale));

            Map<String, Object> sizeData = new HashMap<>();
            sizeData.put("width", cp.getSize().getWidth());
            sizeData.put("height", cp.getSize().getHeight());
            productData.put("size", sizeData);

            productsInCartList.add(productData);
        }

        response.put("productsInCartList", productsInCartList);
        response.put("cartProductCount", cart.getProductCount());
        response.put("subTotalPrice", PriceFormatter.format(cart.getSubTotalPrice(), locale));
        response.put("unitLength", unitLengthForLocale(locale));
        return response;
    }

    @PostMapping("/{locale}/addToCart")
    @ResponseBody
    public Map<String, Object> addToCart(@PathVariable String locale,
                                         @RequestParam int productId,
                                         @RequestParam String finish,
                                         @RequestParam int sizeId,
                                         HttpSession session)
    {
        Cart cart = sessionService.getCart(session);
        Product product = productRepository.findById(productId).orElse(null);
        PosterSize size = posterSizeRepository.findById(sizeId).orElse(null);
        Map<String, Object> response = new HashMap<>();

        if (product == null || size == null)
        {
            response.put("success", false);
            return response;
        }

        // Check if item already in cart
        CartProduct existing = cartProductRepository.findByCartAndProductAndFinishAndSize(cart, product, finish, size);
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
            cp.setSize(size);
            cp.setProductCount(1);
            ProductPosterSize pps = productPosterSizeRepository.findByProductAndSize(product, size);
            cp.setPrice(pps != null ? pps.getPrice() : 0);
            cartProductRepository.save(cp);
        }

        // Recalculate prices
        double itemPrice = 0;
        ProductPosterSize pps = productPosterSizeRepository.findByProductAndSize(product, size);
        itemPrice = pps != null ? pps.getPrice() : 0;
        cart.setSubTotalPrice(cart.getSubTotalPrice() + itemPrice);
        cart.calculateTotalTaxPrice();
        cart.calculateTotalPrice();
        cartRepository.save(cart);

        response.put("success", true);
        response.put("cartProductCount", cart.getProductCount());
        response.put("subTotalPrice", cart.getSubTotalPriceAsString());
        response.put("totalPrice", cart.getTotalPriceAsString());
        return response;
    }

    @PostMapping("/{locale}/updateProductCount")
    @ResponseBody
    public Map<String, Object> updateProductCount(@PathVariable String locale,
                                                   @RequestParam int cartProductId,
                                                   @RequestParam int productCount,
                                                   HttpSession session)
    {
        CartProduct cp = cartProductRepository.findById(cartProductId).orElse(null);
        Cart cart = sessionService.getCart(session);
        Map<String, Object> response = new HashMap<>();

        if (cp == null)
        {
            response.put("success", false);
            return response;
        }

        double priceDiff = cp.getPrice() * (productCount - cp.getProductCount());
        cp.setProductCount(productCount);
        cartProductRepository.save(cp);

        cart.setSubTotalPrice(cart.getSubTotalPrice() + priceDiff);
        cart.calculateTotalTaxPrice();
        cart.calculateTotalPrice();
        cartRepository.save(cart);

        response.put("success", true);
        response.put("cartProductCount", cart.getProductCount());
        response.put("subTotalPrice", cart.getSubTotalPriceAsString());
        response.put("totalTaxPrice", cart.getTotalTaxPriceAsString());
        response.put("totalPrice", cart.getTotalPriceAsString());
        response.put("totalUnitPrice", cp.getTotalProductPriceAsString());
        return response;
    }

    @PostMapping("/{locale}/deleteFromCart")
    @ResponseBody
    public Map<String, Object> deleteFromCart(@PathVariable String locale,
                                              @RequestParam int cartProductId,
                                              HttpSession session)
    {
        CartProduct cp = cartProductRepository.findById(cartProductId).orElse(null);
        Cart cart = sessionService.getCart(session);
        Map<String, Object> response = new HashMap<>();

        if (cp != null)
        {
            cart.setSubTotalPrice(cart.getSubTotalPrice() - (cp.getPrice() * cp.getProductCount()));
            cart.getProducts().remove(cp);
            cartProductRepository.delete(cp);
            cart.calculateTotalTaxPrice();
            cart.calculateTotalPrice();
            cartRepository.save(cart);
        }

        response.put("success", true);
        response.put("cartProductCount", cart.getProductCount());
        response.put("subTotalPrice", cart.getSubTotalPriceAsString());
        response.put("totalTaxPrice", cart.getTotalTaxPriceAsString());
        response.put("totalPrice", cart.getTotalPriceAsString());
        return response;
    }

    @GetMapping("/{locale}/miniCart")
    @ResponseBody
    public Map<String, Object> miniCart(@PathVariable String locale, HttpSession session)
    {
        Cart cart = sessionService.getCart(session);
        Map<String, Object> response = new HashMap<>();
        response.put("cartProductCount", cart.getProductCount());
        response.put("subTotalPrice", cart.getSubTotalPriceAsString());
        response.put("totalPrice", cart.getTotalPriceAsString());
        return response;
    }

    private String unitLengthForLocale(String locale)
    {
        if (locale.startsWith("de") || locale.equals("en-UK") || locale.equals("sv-SE")) return "cm";
        return props.getUnitOfLength();
    }
}
