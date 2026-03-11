package com.xceptance.posters.entity;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Converts a completed CatalogCart into an immutable CatalogOrder snapshot.
 * All checkout data (addresses, credit card, line items, totals) is copied
 * from the cart into the order's own snapshot tables, decoupling the order
 * from any future changes to the source entities.
 */
public class CartToOrderConverter {

    private static final AtomicLong ORDER_SEQ = new AtomicLong(System.currentTimeMillis());

    /**
     * Generates a unique, customer-facing order number.
     */
    public static String generateOrderNumber() {
        return "ORD-" + ORDER_SEQ.incrementAndGet();
    }

    /**
     * Creates a CatalogOrder from a completed cart.
     *
     * @param cart the completed cart with price table, addresses, credit card, and line items
     * @param currency the currency code (e.g. "USD", "EUR")
     * @param customerEmail the customer's email for the order snapshot
     * @param customerFirstName the customer's first name
     * @param customerLastName the customer's last name
     * @return a fully populated CatalogOrder (not yet persisted)
     */
    public static CatalogOrder convert(CatalogCart cart, String currency,
                                        String customerEmail,
                                        String customerFirstName,
                                        String customerLastName) {

        CatalogOrder order = new CatalogOrder();
        order.setOrderNumber(generateOrderNumber());
        order.setCurrency(currency);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderState("created");
        order.setPaymentState("authorized");

        // Copy monetary totals
        order.setSubTotal(cart.getSubTotal());
        order.setShippingCosts(cart.getShippingCosts());
        order.setTaxRate(cart.getTaxRate());
        order.setTotalTax(cart.getTotalTax());
        order.setTotal(cart.getTotal());

        // Snapshot shipping method
        if (cart.getShippingMethod() != null) {
            order.setShippingMethodSku(cart.getShippingMethod().getSku());
        }

        // Snapshot line items
        for (CartLineItem cartItem : cart.getLineItems()) {
            OrderLineItem orderItem = new OrderLineItem();
            orderItem.setSku(cartItem.getSku());
            orderItem.setProductName(cartItem.getSku()); // placeholder — real impl would look up product name
            orderItem.setQuantity(cartItem.getQuantity());
            // Price would be looked up from the price table in a real implementation
            orderItem.setUnitPrice(java.math.BigDecimal.ZERO);
            orderItem.setTotalPrice(java.math.BigDecimal.ZERO);
            order.addLineItem(orderItem);
        }

        // Snapshot shipping address
        if (cart.getShippingAddress() != null) {
            order.addStateHistoryEntry(createInitialStateEntry());
            snapshotAddress(order, cart.getShippingAddress(), "SHIPPING");
        }

        // Snapshot billing address
        if (cart.getBillingAddress() != null) {
            snapshotAddress(order, cart.getBillingAddress(), "BILLING");
        }

        // Initial state history
        OrderStateHistory stateEntry = createInitialStateEntry();
        order.addStateHistoryEntry(stateEntry);

        // Initial payment history
        OrderPaymentHistory paymentEntry = new OrderPaymentHistory();
        paymentEntry.setOldState(null);
        paymentEntry.setNewState("authorized");
        order.addPaymentHistoryEntry(paymentEntry);

        // Snapshot credit card
        if (cart.getCreditCard() != null) {
            CartCreditCard cartCard = cart.getCreditCard();
            OrderCreditCard orderCard = new OrderCreditCard();
            orderCard.setNumber(cartCard.getNumber());
            orderCard.setVendor(cartCard.getVendor());
            orderCard.setName(cartCard.getName());
            orderCard.setExpMonth(cartCard.getExpMonth());
            orderCard.setExpYear(cartCard.getExpYear());
            order.setCreditCard(orderCard);
        }

        return order;
    }

    private static OrderStateHistory createInitialStateEntry() {
        OrderStateHistory entry = new OrderStateHistory();
        entry.setOldState(null);
        entry.setNewState("created");
        return entry;
    }

    private static void snapshotAddress(CatalogOrder order, CartAddress source, String type) {
        OrderAddress addr = new OrderAddress();
        addr.setType(type);
        addr.setRecipientFirstName(source.getRecipientFirstName());
        addr.setRecipientLastName(source.getRecipientLastName());
        addr.setCompany(source.getCompany());
        addr.setAddressLine1(source.getAddressLine1());
        addr.setAddressLine2(source.getAddressLine2());
        addr.setCity(source.getCity());
        addr.setState(source.getState());
        addr.setPostalCode(source.getPostalCode());
        addr.setCountry(source.getCountry());
        addr.setPhone(source.getPhone());
        order.addAddress(addr);
    }
}
