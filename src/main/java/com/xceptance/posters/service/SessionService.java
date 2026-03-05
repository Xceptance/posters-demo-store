package com.xceptance.posters.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.model.Cart;
import com.xceptance.posters.repository.CartRepository;

import jakarta.servlet.http.HttpSession;

/**
 * Session management service — replaces the old static SessionHandling utility.
 * Uses HttpSession to store cart, order, and customer IDs.
 */
@Service
public class SessionService
{
    private static final String CART_KEY = "cartId";
    private static final String ORDER_KEY = "orderId";
    private static final String CUSTOMER_KEY = "customerId";

    private final CartRepository cartRepository;
    private final PostersProperties props;

    public SessionService(CartRepository cartRepository, PostersProperties props)
    {
        this.cartRepository = cartRepository;
        this.props = props;
    }

    // --- Cart ---

    public UUID getCartId(HttpSession session)
    {
        UUID cartId = (UUID) session.getAttribute(CART_KEY);
        if (cartId == null || !cartRepository.existsById(cartId))
        {
            Cart cart = createNewCart();
            session.setAttribute(CART_KEY, cart.getId());
            return cart.getId();
        }
        return cartId;
    }

    public Cart getCart(HttpSession session)
    {
        UUID cartId = getCartId(session);
        return cartRepository.findById(cartId).orElseGet(this::createNewCart);
    }

    public void setCartId(HttpSession session, UUID cartId)
    {
        session.setAttribute(CART_KEY, cartId);
    }

    public void removeCartId(HttpSession session)
    {
        session.removeAttribute(CART_KEY);
    }

    private Cart createNewCart()
    {
        Cart cart = new Cart();
        cart.setSubTotalPrice(0);
        cart.setTotalPrice(0);
        cart.setTotalTaxPrice(0);
        cart.setTax(props.getTax());
        cart.setShippingCosts(props.getShippingCosts());
        return cartRepository.save(cart);
    }

    // --- Customer ---

    public boolean isCustomerLoggedIn(HttpSession session)
    {
        return session.getAttribute(CUSTOMER_KEY) != null;
    }

    public UUID getCustomerId(HttpSession session)
    {
        return (UUID) session.getAttribute(CUSTOMER_KEY);
    }

    public void setCustomerId(HttpSession session, UUID customerId)
    {
        session.setAttribute(CUSTOMER_KEY, customerId);
    }

    public void removeCustomerId(HttpSession session)
    {
        session.removeAttribute(CUSTOMER_KEY);
    }

    // --- Order ---

    public boolean isOrderIdSet(HttpSession session)
    {
        return session.getAttribute(ORDER_KEY) != null;
    }

    public UUID getOrderId(HttpSession session)
    {
        return (UUID) session.getAttribute(ORDER_KEY);
    }

    public void setOrderId(HttpSession session, UUID orderId)
    {
        session.setAttribute(ORDER_KEY, orderId);
    }

    public void removeOrderId(HttpSession session)
    {
        session.removeAttribute(ORDER_KEY);
    }
}
