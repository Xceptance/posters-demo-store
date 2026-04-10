package com.xceptance.posters.entity;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Objects;
import java.math.BigDecimal;
import com.xceptance.posters.dto.CartDto;

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
     * @param cartDto the resolved Data Transfer Object holding dynamically evaluated properties
     * @param customerEmail the customer's email for the order snapshot
     * @param customerFirstName the customer's first name
     * @param customerLastName the customer's last name
     * @return a fully populated CatalogOrder (not yet persisted)
     */
    public static CatalogOrder convert(CatalogCart cart, CartDto cartDto, String currency,
                                        String customerEmail,
                                        String customerFirstName,
                                        String customerLastName) {

        CatalogOrder order = new CatalogOrder();
        order.setOrderNumber(generateOrderNumber());
        order.setCurrency(currency);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderState("created");
        order.setPaymentState("authorized");

        if (customerEmail != null) {
            OrderCustomer orderCustomer = new OrderCustomer();
            orderCustomer.setEmail(customerEmail);
            orderCustomer.setFirstName(customerFirstName);
            orderCustomer.setLastName(customerLastName);
            order.setCustomer(orderCustomer);
        }

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
            orderItem.setQuantity(cartItem.getQuantity());
            
            // Map the persisted properties directly to the snapshot!
            orderItem.setProductName(cartItem.getProductName() != null ? cartItem.getProductName() : cartItem.getSku());
            
            Objects.requireNonNull(cartDto, "CartDto payload cannot be null during order creation to ensure visual snapshot integrity");

            // Extract the dynamic image and variant properties from the resolved DTO format securely
            cartDto.products().stream()
                .filter(dto -> dto.sku().equals(cartItem.getSku()) && dto.lineItemId() == (cartItem.getId() != null ? cartItem.getId() : 0))
                .findFirst()
                .ifPresent(dto -> {
                    orderItem.setImageUrl(dto.getImageURL());
                    String finish = dto.finish() != null && !dto.finish().isEmpty() ? dto.finish() : "";
                    String size = dto.sizeLabel() != null && !dto.sizeLabel().isEmpty() ? dto.sizeLabel() : "";
                    String variantDesc = size + (size.isEmpty() || finish.isEmpty() ? "" : ", ") + finish;
                    orderItem.setVariantDescription(variantDesc.trim().isEmpty() ? null : variantDesc.trim());
                });

            BigDecimal unitPrice = cartItem.getUnitPrice() != null ? cartItem.getUnitPrice() : BigDecimal.ZERO;
            orderItem.setUnitPrice(unitPrice);
            orderItem.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            
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
