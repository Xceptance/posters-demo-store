package com.xceptance.posters.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

import java.util.Optional;

/**
 * Service layer for checkout operations using the new entity model.
 * Handles cart management and cart-to-order conversion.
 */
@Service
public class CheckoutService {

    private static final Logger log = LoggerFactory.getLogger(CheckoutService.class);

    private final CatalogCartRepository cartRepository;
    private final CatalogOrderRepository orderRepository;
    private final EntityManager entityManager;

    public CheckoutService(CatalogCartRepository cartRepository,
                           CatalogOrderRepository orderRepository,
                           EntityManager entityManager) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.entityManager = entityManager;
    }

    /**
     * Saves or updates a cart.
     */
    @Transactional
    public CatalogCart saveCart(CatalogCart cart) {
        log.info("Saving cart: {}", cart.getId());
        return cartRepository.save(cart);
    }

    /**
     * Retrieves a cart by its ID.
     */
    @Transactional(readOnly = true)
    public Optional<CatalogCart> getCart(java.util.UUID cartId) {
        return cartRepository.findById(cartId);
    }

    /**
     * Converts a cart to an order using CartToOrderConverter, persists the order,
     * and deletes the cart.
     *
     * @param cartId the UUID of the cart to checkout
     * @param customerEmail the customer's email
     * @param customerFirstName the customer's first name
     * @param customerLastName the customer's last name
     * @return the persisted CatalogOrder
     * @throws RuntimeException if the cart does not exist
     */
    @Transactional
    public CatalogOrder checkout(java.util.UUID cartId,
                                  String customerEmail,
                                  String customerFirstName,
                                  String customerLastName) {
        CatalogCart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> {
                String msg = "ERROR: Cart not found for checkout: " + cartId;
                log.error(msg);
                return new RuntimeException(msg);
            });

        CatalogOrder order = CartToOrderConverter.convert(cart, "USD",
            customerEmail, customerFirstName, customerLastName);
        orderRepository.save(order);

        // 1. Clear FK references on cart so child rows can be deleted
        entityManager.createNativeQuery("UPDATE catalog_carts SET shipping_address_id = NULL, billing_address_id = NULL, credit_card_id = NULL WHERE id = :cid")
            .setParameter("cid", cart.getId()).executeUpdate();
        // 2. Delete child entities
        entityManager.createNativeQuery("DELETE FROM catalog_cart_lineitems WHERE cart_id = :cid")
            .setParameter("cid", cart.getId()).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM catalog_cart_credit_cards WHERE cart_id = :cid")
            .setParameter("cid", cart.getId()).executeUpdate();
        entityManager.createNativeQuery("DELETE FROM catalog_cart_addresses WHERE cart_id = :cid")
            .setParameter("cid", cart.getId()).executeUpdate();
        // 3. Delete the cart itself
        entityManager.createNativeQuery("DELETE FROM catalog_carts WHERE id = :cid")
            .setParameter("cid", cart.getId()).executeUpdate();
        entityManager.flush();
        entityManager.clear();

        log.info("Checkout complete: cart {} → order {}", cartId, order.getOrderNumber());
        return order;
    }

    /**
     * Retrieves an order by its ID.
     */
    @Transactional(readOnly = true)
    public Optional<CatalogOrder> getOrder(java.util.UUID orderId) {
        return orderRepository.findById(orderId);
    }

    /**
     * Retrieves an order by its order number.
     */
    @Transactional(readOnly = true)
    public Optional<CatalogOrder> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }
}
