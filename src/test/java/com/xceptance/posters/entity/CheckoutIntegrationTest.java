package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test: Full checkout flow from Cart → Order persistence.
 * Covers the end-to-end lifecycle: build cart, convert to order, persist, reload.
 */
@DataJpaTest
class CheckoutIntegrationTest {

    @Autowired
    private TestEntityManager em;

    @Test
    void testFullCheckoutFlow() {
        // 1. Build a cart with line items
        CatalogCart cart = new CatalogCart();
        cart = em.persistAndFlush(cart);

        CartLineItem item1 = new CartLineItem();
        item1.setSku("POSTER01-0001");
        item1.setQuantity(2);
        cart.addLineItem(item1);

        CartLineItem item2 = new CartLineItem();
        item2.setSku("POSTER02-0003");
        item2.setQuantity(1);
        cart.addLineItem(item2);

        // 2. Set checkout addresses
        CartAddress shippingAddr = new CartAddress();
        shippingAddr.setCart(cart);
        shippingAddr.setRecipientFirstName("Jane");
        shippingAddr.setRecipientLastName("Doe");
        shippingAddr.setAddressLine1("123 Checkout Blvd");
        shippingAddr.setCity("Checkoutville");
        shippingAddr.setState("TX");
        shippingAddr.setPostalCode("77001");
        shippingAddr.setCountry("US");
        shippingAddr = em.persist(shippingAddr);
        cart.setShippingAddress(shippingAddr);

        // 3. Set totals
        cart.setSubTotal(new BigDecimal("74.97"));
        cart.setShippingCosts(new BigDecimal("7.99"));
        cart.setTaxRate(new BigDecimal("0.0825"));
        cart.setTotalTax(new BigDecimal("6.19"));
        cart.setTotal(new BigDecimal("89.15"));

        // 4. Set shipping method
        ShippingMethod sm = new ShippingMethod();
        sm.setSku("SHIP-EXP");
        sm = em.persistAndFlush(sm);
        cart.setShippingMethod(sm);

        em.persistAndFlush(cart);

        // 5. Convert cart to order
        CatalogOrder order = CartToOrderConverter.convert(
            cart, "USD", "jane@example.com", "Jane", "Doe"
        );

        // 6. Persist the order
        order = em.persistAndFlush(order);
        em.clear();

        // 7. Reload and verify
        CatalogOrder reloaded = em.find(CatalogOrder.class, order.getId());
        assertThat(reloaded).isNotNull();
        assertThat(reloaded.getOrderNumber()).startsWith("ORD-");
        assertThat(reloaded.getCurrency()).isEqualTo("USD");
        assertThat(reloaded.getTotal()).isEqualByComparingTo(new BigDecimal("89.15"));
        assertThat(reloaded.getShippingMethodSku()).isEqualTo("SHIP-EXP");

        // Verify line items were snapshotted
        assertThat(reloaded.getLineItems()).hasSize(2);
        assertThat(reloaded.getLineItems().get(0).getSku()).isEqualTo("POSTER01-0001");

        // Verify state history
        assertThat(reloaded.getStateHistory()).isNotEmpty();
        assertThat(reloaded.getStateHistory().get(0).getNewState()).isEqualTo("created");

        // Verify payment history
        assertThat(reloaded.getPaymentHistory()).hasSize(1);
        assertThat(reloaded.getPaymentHistory().get(0).getNewState()).isEqualTo("authorized");
    }

    @Test
    void testOrderImmutability() {
        // Create and persist an order
        CatalogOrder order = new CatalogOrder();
        order.setOrderNumber("IMM-001");
        order.setCurrency("EUR");
        order.setOrderState("created");
        order.setPaymentState("authorized");
        order.setTotal(new BigDecimal("100.00"));
        order = em.persistAndFlush(order);

        // Add state history entries to simulate lifecycle
        OrderStateHistory h1 = new OrderStateHistory();
        h1.setOldState(null);
        h1.setNewState("created");
        order.addStateHistoryEntry(h1);

        OrderStateHistory h2 = new OrderStateHistory();
        h2.setOldState("created");
        h2.setNewState("new");
        order.addStateHistoryEntry(h2);

        order.setOrderState("new");
        em.persistAndFlush(order);
        em.clear();

        CatalogOrder reloaded = em.find(CatalogOrder.class, order.getId());
        assertThat(reloaded.getOrderState()).isEqualTo("new");
        assertThat(reloaded.getStateHistory()).hasSize(2);

        // History is append-only — old entries preserved
        assertThat(reloaded.getStateHistory().get(0).getOldState()).isNull();
        assertThat(reloaded.getStateHistory().get(1).getOldState()).isEqualTo("created");
    }

    @Test
    void testOrderWithCustomerAndAddressSnapshots() {
        CatalogOrder order = new CatalogOrder();
        order.setOrderNumber("SNAP-001");
        order.setCurrency("USD");
        order.setOrderState("created");
        order.setPaymentState("authorized");
        order.setTotal(new BigDecimal("50.00"));
        order = em.persistAndFlush(order);

        // Snapshot customer
        OrderCustomer custSnapshot = new OrderCustomer();
        custSnapshot.setOrder(order);
        custSnapshot.setEmail("snapshot@test.com");
        custSnapshot.setFirstName("Snap");
        custSnapshot.setLastName("Shot");
        em.persist(custSnapshot);

        // Snapshot shipping address
        OrderAddress shipAddr = new OrderAddress();
        shipAddr.setOrder(order);
        shipAddr.setType("SHIPPING");
        shipAddr.setRecipientFirstName("Snap");
        shipAddr.setRecipientLastName("Shot");
        shipAddr.setAddressLine1("1 Immutable Lane");
        shipAddr.setCity("Snaptown");
        shipAddr.setState("IL");
        shipAddr.setPostalCode("60601");
        shipAddr.setCountry("US");
        em.persist(shipAddr);

        // Snapshot credit card
        OrderCreditCard ccSnapshot = new OrderCreditCard();
        ccSnapshot.setOrder(order);
        ccSnapshot.setNumber("555555******4444");
        ccSnapshot.setVendor("Mastercard");
        ccSnapshot.setName("Snap Shot");
        ccSnapshot.setExpMonth(3);
        ccSnapshot.setExpYear(2029);
        em.persistAndFlush(ccSnapshot);
        em.clear();

        // Verify snapshots are persisted and independent
        OrderCustomer reloadedCust = em.find(OrderCustomer.class, custSnapshot.getId());
        assertThat(reloadedCust.getEmail()).isEqualTo("snapshot@test.com");

        OrderAddress reloadedAddr = em.find(OrderAddress.class, shipAddr.getId());
        assertThat(reloadedAddr.getCity()).isEqualTo("Snaptown");

        OrderCreditCard reloadedCC = em.find(OrderCreditCard.class, ccSnapshot.getId());
        assertThat(reloadedCC.getVendor()).isEqualTo("Mastercard");
    }
}
