package com.xceptance.posters.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.entity.CatalogCart;
import com.xceptance.posters.entity.CatalogCartRepository;

import jakarta.servlet.http.HttpSession;

/**
 * Session management service — uses HttpSession to store cart, order, and customer IDs.
 * Now uses CatalogCart (new entity model) instead of legacy model.Cart.
 */
@Service
public class SessionService
{
    private static final String CART_KEY = "cartId";
    private static final String ORDER_KEY = "orderId";
    private static final String CUSTOMER_KEY = "customerId";

    private final CatalogCartRepository cartRepository;
    private final PostersProperties props;

    public SessionService(CatalogCartRepository cartRepository, PostersProperties props)
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
            CatalogCart cart = createNewCart();
            session.setAttribute(CART_KEY, cart.getId());
            return cart.getId();
        }
        return cartId;
    }

    public CatalogCart getCart(HttpSession session)
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

    private CatalogCart createNewCart()
    {
        CatalogCart cart = new CatalogCart();
        cart.setSubTotal(BigDecimal.ZERO);
        cart.setTotal(BigDecimal.ZERO);
        cart.setTotalTax(BigDecimal.ZERO);
        cart.setTaxRate(props.getTax());
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
