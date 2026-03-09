package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderTest {

    @Autowired
    private TestEntityManager em;

    private CatalogOrder createOrder(String orderNumber) {
        CatalogOrder order = new CatalogOrder();
        order.setOrderNumber(orderNumber);
        order.setCurrency("USD");
        order.setOrderState("created");
        order.setPaymentState("authorized");
        order.setSubTotal(new BigDecimal("49.98"));
        order.setShippingCosts(new BigDecimal("5.99"));
        order.setTaxRate(new BigDecimal("0.0725"));
        order.setTotalTax(new BigDecimal("3.62"));
        order.setTotal(new BigDecimal("59.59"));
        return em.persistAndFlush(order);
    }

    @Test
    void testCreateOrder() {
        CatalogOrder order = createOrder("ORD-20260307-001");
        assertThat(order.getId()).isNotNull();
        assertThat(order.getOrderNumber()).isEqualTo("ORD-20260307-001");
        assertThat(order.getOrderDate()).isNotNull();
        assertThat(order.getTotal()).isEqualByComparingTo(new BigDecimal("59.59"));
    }

    @Test
    void testOrderWithLineItems() {
        CatalogOrder order = createOrder("ORD-LI-001");

        OrderLineItem item = new OrderLineItem();
        item.setSku("POSTER01-0001");
        item.setProductName("Sunset Beach");
        item.setVariantDescription("16x20, matte");
        item.setQuantity(2);
        item.setUnitPrice(new BigDecimal("24.99"));
        item.setTotalPrice(new BigDecimal("49.98"));
        order.addLineItem(item);

        em.persistAndFlush(order);
        em.clear();

        CatalogOrder reloaded = em.find(CatalogOrder.class, order.getId());
        assertThat(reloaded.getLineItems()).hasSize(1);
        assertThat(reloaded.getLineItems().get(0).getProductName()).isEqualTo("Sunset Beach");
    }

    @Test
    void testOrderStateHistory() {
        CatalogOrder order = createOrder("ORD-HIST-001");

        OrderStateHistory h1 = new OrderStateHistory();
        h1.setOldState(null);
        h1.setNewState("created");
        order.addStateHistoryEntry(h1);

        OrderStateHistory h2 = new OrderStateHistory();
        h2.setOldState("created");
        h2.setNewState("new");
        order.addStateHistoryEntry(h2);

        em.persistAndFlush(order);
        em.clear();

        CatalogOrder reloaded = em.find(CatalogOrder.class, order.getId());
        assertThat(reloaded.getStateHistory()).hasSize(2);
        assertThat(reloaded.getStateHistory().get(0).getNewState()).isEqualTo("created");
        assertThat(reloaded.getStateHistory().get(1).getOldState()).isEqualTo("created");
    }

    @Test
    void testOrderPaymentHistory() {
        CatalogOrder order = createOrder("ORD-PAY-001");

        OrderPaymentHistory p1 = new OrderPaymentHistory();
        p1.setOldState(null);
        p1.setNewState("authorized");
        order.addPaymentHistoryEntry(p1);

        em.persistAndFlush(order);
        em.clear();

        CatalogOrder reloaded = em.find(CatalogOrder.class, order.getId());
        assertThat(reloaded.getPaymentHistory()).hasSize(1);
        assertThat(reloaded.getPaymentHistory().get(0).getNewState()).isEqualTo("authorized");
    }

    @Test
    void testOrderWithCustomerSnapshot() {
        CatalogOrder order = createOrder("ORD-CUST-001");

        OrderCustomer customer = new OrderCustomer();
        customer.setOrder(order);
        customer.setEmail("buyer@example.com");
        customer.setFirstName("Jane");
        customer.setLastName("Doe");
        em.persistAndFlush(customer);

        em.clear();

        OrderCustomer reloaded = em.find(OrderCustomer.class, customer.getId());
        assertThat(reloaded.getEmail()).isEqualTo("buyer@example.com");
    }

    @Test
    void testOrderAddressSnapshots() {
        CatalogOrder order = createOrder("ORD-ADDR-001");

        OrderAddress shipping = new OrderAddress();
        shipping.setOrder(order);
        shipping.setType("SHIPPING");
        shipping.setRecipientFirstName("Jane");
        shipping.setRecipientLastName("Doe");
        shipping.setAddressLine1("123 Ship St");
        shipping.setCity("Shiptown");
        shipping.setState("CA");
        shipping.setPostalCode("90210");
        shipping.setCountry("US");
        em.persist(shipping);

        OrderAddress billing = new OrderAddress();
        billing.setOrder(order);
        billing.setType("BILLING");
        billing.setRecipientFirstName("Jane");
        billing.setRecipientLastName("Doe");
        billing.setAddressLine1("456 Bill Ave");
        billing.setCity("Billville");
        billing.setState("NY");
        billing.setPostalCode("10001");
        billing.setCountry("US");
        em.persistAndFlush(billing);

        em.clear();

        OrderAddress reShip = em.find(OrderAddress.class, shipping.getId());
        OrderAddress reBill = em.find(OrderAddress.class, billing.getId());
        assertThat(reShip.getType()).isEqualTo("SHIPPING");
        assertThat(reBill.getType()).isEqualTo("BILLING");
    }
}
