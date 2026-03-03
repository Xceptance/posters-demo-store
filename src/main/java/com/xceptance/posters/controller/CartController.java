package com.xceptance.posters.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

import java.util.HashMap;
import java.util.Map;

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

    public CartController(CartRepository cartRepository,
                          CartProductRepository cartProductRepository,
                          ProductRepository productRepository,
                          PosterSizeRepository posterSizeRepository,
                          ProductPosterSizeRepository productPosterSizeRepository,
                          SessionService sessionService)
    {
        this.cartRepository = cartRepository;
        this.cartProductRepository = cartProductRepository;
        this.productRepository = productRepository;
        this.posterSizeRepository = posterSizeRepository;
        this.productPosterSizeRepository = productPosterSizeRepository;
        this.sessionService = sessionService;
    }

    @GetMapping("/{locale}/cart")
    public String viewCart(@PathVariable String locale, HttpSession session, Model model)
    {
        Cart cart = sessionService.getCart(session);
        model.addAttribute("cart", cart);
        return "cart/cart";
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
            // Get price from ProductPosterSize
            ProductPosterSize pps = productPosterSizeRepository.findByProductAndSize(product, size);
            cp.setPrice(pps != null ? pps.getPrice() : 0);
            cartProductRepository.save(cp);
        }

        // Recalculate prices
        double itemPrice = (existing != null) ? existing.getPrice() : 0;
        if (existing == null)
        {
            ProductPosterSize pps = productPosterSizeRepository.findByProductAndSize(product, size);
            itemPrice = pps != null ? pps.getPrice() : 0;
        }
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
}
