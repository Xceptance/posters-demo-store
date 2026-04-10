package com.xceptance.posters.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.xceptance.posters.dto.CartDto;
import com.xceptance.posters.dto.CartItemDto;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for CartToOrderConverter — pure Java, no Spring context needed.
 */
class CartToOrderConverterTest {

    private CatalogCart cart;
    private CartDto emptyCartDto;

    @BeforeEach
    void setUp() {
        emptyCartDto = new CartDto(List.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "0", 0);
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
        CatalogOrder order = CartToOrderConverter.convert(cart, emptyCartDto, "USD", "test@example.com", "Test", "User");

        assertThat(order.getSubTotal()).isEqualByComparingTo(new BigDecimal("49.98"));
        assertThat(order.getShippingCosts()).isEqualByComparingTo(new BigDecimal("5.99"));
        assertThat(order.getTaxRate()).isEqualByComparingTo(new BigDecimal("0.0725"));
        assertThat(order.getTotalTax()).isEqualByComparingTo(new BigDecimal("3.62"));
        assertThat(order.getTotal()).isEqualByComparingTo(new BigDecimal("59.59"));
    }

    @Test
    void testConvertSetsOrderMetadata() {
        CatalogOrder order = CartToOrderConverter.convert(cart, emptyCartDto, "EUR", "buyer@shop.de", "Hans", "Mueller");

        assertThat(order.getOrderNumber()).startsWith("ORD-");
        assertThat(order.getCurrency()).isEqualTo("EUR");
        assertThat(order.getOrderState()).isEqualTo("created");
        assertThat(order.getPaymentState()).isEqualTo("authorized");
        assertThat(order.getOrderDate()).isNotNull();
    }

    @Test
    void testConvertSnapshotsLineItems() {
        CatalogOrder order = CartToOrderConverter.convert(cart, emptyCartDto, "USD", "test@example.com", "Test", "User");

        assertThat(order.getLineItems()).hasSize(2);
        assertThat(order.getLineItems().get(0).getSku()).isEqualTo("POSTER01-0001");
        assertThat(order.getLineItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(order.getLineItems().get(1).getSku()).isEqualTo("POSTER02-0003");
    }

    @Test
    void testConvertSnapshotsImagesAndVariantsFromDto() {
        CartItemDto item1 = new CartItemDto(
            0, 0, "Test Poster", "https://cdn.example.com/p1.webp",
            "Matte", "16x12", BigDecimal.ZERO, 1, BigDecimal.ZERO, "POSTER01-0001"
        );
        CartItemDto item2 = new CartItemDto(
            0, 0, "Second Poster", null,
            "", "", BigDecimal.ZERO, 1, BigDecimal.ZERO, "POSTER02-0003"
        );
        CartDto cartDto = new CartDto(
            List.of(item1, item2), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, "0", 2
        );

        CatalogOrder order = CartToOrderConverter.convert(cart, cartDto, "USD", "test@example.com", "Test", "User");

        assertThat(order.getLineItems()).hasSize(2);
        
        OrderLineItem firstItem = order.getLineItems().stream().filter(li -> li.getSku().equals("POSTER01-0001")).findFirst().get();
        assertThat(firstItem.getImageUrl()).isEqualTo("https://cdn.example.com/p1.webp");
        assertThat(firstItem.getVariantDescription()).isEqualTo("16x12, Matte");

        OrderLineItem secondItem = order.getLineItems().stream().filter(li -> li.getSku().equals("POSTER02-0003")).findFirst().get();
        assertThat(secondItem.getImageUrl()).isNull();
        assertThat(secondItem.getVariantDescription()).isNull(); // Empty variants gracefully mapped to null strings
    }

    @Test
    void testConvertCreatesHistoryEntries() {
        CatalogOrder order = CartToOrderConverter.convert(cart, emptyCartDto, "USD", "test@example.com", "Test", "User");

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

        CatalogOrder order = CartToOrderConverter.convert(cart, emptyCartDto, "USD", "test@example.com", "Test", "User");

        assertThat(order.getShippingMethodSku()).isEqualTo("SHIP-STD");
    }

    @Test
    void testConvertWithNullCartDtoThrowsException() {
        assertThrows(NullPointerException.class, () -> {
            CartToOrderConverter.convert(cart, null, "USD", "test@example.com", "Test", "User");
        }, "Converter should natively block null UI layouts from circumventing image bindings.");
    }
}
