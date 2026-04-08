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
    private final com.xceptance.posters.jmx.OrderProcessingMetrics orderProcessingMetrics;

    public CheckoutService(CatalogCartRepository cartRepository,
                           CatalogOrderRepository orderRepository,
                           EntityManager entityManager,
                           com.xceptance.posters.jmx.OrderProcessingMetrics orderProcessingMetrics) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
        this.entityManager = entityManager;
        this.orderProcessingMetrics = orderProcessingMetrics;
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
                                  String customerLastName) 
    {
        com.xceptance.posters.jfr.OrderProcessingEvent jfrEvent = new com.xceptance.posters.jfr.OrderProcessingEvent();
        jfrEvent.begin();
        try 
        {
            CatalogCart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> 
                {
                    String msg = "ERROR: Cart not found for checkout: " + cartId;
                    log.error(msg);
                    return new RuntimeException(msg);
                });

            CatalogOrder order = CartToOrderConverter.convert(cart, "USD",
                customerEmail, customerFirstName, customerLastName);
            orderRepository.save(order);

            // JFR Data Population
            jfrEvent.orderId = order.getOrderNumber();
            jfrEvent.itemCount = order.getLineItems().stream().mapToInt(OrderLineItem::getQuantity).sum();
            jfrEvent.totalAmount = order.getTotal() != null ? order.getTotal().doubleValue() : 0.0;
            if (order.getCreditCard() != null) 
            {
                jfrEvent.creditCardVendor = order.getCreditCard().getVendor();
            }

            // JMX MBean Data Population
            orderProcessingMetrics.recordOrder(
                jfrEvent.orderId, 
                jfrEvent.itemCount, 
                jfrEvent.totalAmount, 
                jfrEvent.creditCardVendor == null ? "" : jfrEvent.creditCardVendor
            );

            // Artificial demo delays
            simulateProcessingDelay(jfrEvent.itemCount, jfrEvent.creditCardVendor);

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
        finally 
        {
            jfrEvent.commit();
        }
    }

    /**
     * Injects delays for JFR demo purposes.
     */
    private void simulateProcessingDelay(int itemCount, String ccVendor) 
    {
        long delayMs = itemCount * 100L;
        if (itemCount > 20) 
        {
            long extraItems = itemCount - 20;
            delayMs += (long) (extraItems * extraItems * 4.53);
        }
        if ("Amex".equalsIgnoreCase(ccVendor)) 
        {
            delayMs += 1500 + new java.util.Random().nextInt(1000);
        }
        if (delayMs > 0) 
        {
            try 
            {
                Thread.sleep(delayMs);
            } 
            catch (InterruptedException e) 
            {
                Thread.currentThread().interrupt();
            }
        }
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
