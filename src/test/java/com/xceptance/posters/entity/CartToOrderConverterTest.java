package com.xceptance.posters.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CartToOrderConverter — pure Java, no Spring context needed.
 */
class CartToOrderConverterTest {

    private CatalogCart cart;

    @BeforeEach
    void setUp() {
        cart = new CatalogCart();
        cart.setSubTotal(new BigDecimal("49.98"));
        cart.setShippingCosts(new BigDecimal("5.99"));
        cart.setTaxRate(new BigDecimal("0.0725"));
        cart.setTotalTax(new BigDecimal("3.62"));
        cart.setTotal(new BigDecimal("59.59"));

        CartLineItem item1 = new CartLineItem();
        item1.setSku("POSTER01-0001");
        item1.setQuantity(2);
        cart.addLineItem(item1);

        CartLineItem item2 = new CartLineItem();
        item2.setSku("POSTER02-0003");
        item2.setQuantity(1);
        cart.addLineItem(item2);
    }

    @Test
    void testOrderNumberGeneration() {
        String num1 = CartToOrderConverter.generateOrderNumber();
        String num2 = CartToOrderConverter.generateOrderNumber();
        assertThat(num1).startsWith("ORD-");
        assertThat(num1).isNotEqualTo(num2);
    }

    @Test
    void testConvertCopiesMonetaryFields() {
        CatalogOrder order = CartToOrderConverter.convert(cart, "USD", "test@example.com", "Test", "User");

        assertThat(order.getSubTotal()).isEqualByComparingTo(new BigDecimal("49.98"));
        assertThat(order.getShippingCosts()).isEqualByComparingTo(new BigDecimal("5.99"));
        assertThat(order.getTaxRate()).isEqualByComparingTo(new BigDecimal("0.0725"));
        assertThat(order.getTotalTax()).isEqualByComparingTo(new BigDecimal("3.62"));
        assertThat(order.getTotal()).isEqualByComparingTo(new BigDecimal("59.59"));
    }

    @Test
    void testConvertSetsOrderMetadata() {
        CatalogOrder order = CartToOrderConverter.convert(cart, "EUR", "buyer@shop.de", "Hans", "Mueller");

        assertThat(order.getOrderNumber()).startsWith("ORD-");
        assertThat(order.getCurrency()).isEqualTo("EUR");
        assertThat(order.getOrderState()).isEqualTo("created");
        assertThat(order.getPaymentState()).isEqualTo("authorized");
        assertThat(order.getOrderDate()).isNotNull();
    }

    @Test
    void testConvertSnapshotsLineItems() {
        CatalogOrder order = CartToOrderConverter.convert(cart, "USD", "test@example.com", "Test", "User");

        assertThat(order.getLineItems()).hasSize(2);
        assertThat(order.getLineItems().get(0).getSku()).isEqualTo("POSTER01-0001");
        assertThat(order.getLineItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(order.getLineItems().get(1).getSku()).isEqualTo("POSTER02-0003");
    }

    @Test
    void testConvertCreatesHistoryEntries() {
        CatalogOrder order = CartToOrderConverter.convert(cart, "USD", "test@example.com", "Test", "User");

        assertThat(order.getStateHistory()).isNotEmpty();
        assertThat(order.getStateHistory().get(0).getNewState()).isEqualTo("created");

        assertThat(order.getPaymentHistory()).hasSize(1);
        assertThat(order.getPaymentHistory().get(0).getNewState()).isEqualTo("authorized");
    }

    @Test
    void testConvertSnapshotsShippingMethod() {
        ShippingMethod sm = new ShippingMethod();
        sm.setSku("SHIP-STD");
        cart.setShippingMethod(sm);

        CatalogOrder order = CartToOrderConverter.convert(cart, "USD", "test@example.com", "Test", "User");

        assertThat(order.getShippingMethodSku()).isEqualTo("SHIP-STD");
    }
}
