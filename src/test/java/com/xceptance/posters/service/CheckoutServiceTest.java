package com.xceptance.posters.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test for CheckoutService — verifies cart management and checkout flow.
 */
@DataJpaTest
@Import(CheckoutService.class)
class CheckoutServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private CheckoutService checkoutService;

    private CatalogCart createAndPersistCart() {
        CatalogCart cart = new CatalogCart();
        cart.setSubTotal(new BigDecimal("49.95"));
        cart.setTotalTax(new BigDecimal("3.62"));
        cart.setTotal(new BigDecimal("53.57"));
        em.persist(cart);
        em.flush();

        CartLineItem item = new CartLineItem();
        item.setSku("TEST-0001");
        item.setQuantity(2);
        item.setCart(cart);
        cart.getLineItems().add(item);

        CartAddress addr = new CartAddress();
        addr.setCart(cart);
        addr.setRecipientFirstName("John");
        addr.setRecipientLastName("Doe");
        addr.setAddressLine1("123 Main St");
        addr.setCity("Springfield");
        addr.setState("IL");
        addr.setPostalCode("62704");
        addr.setCountry("US");
        em.persist(addr);

        cart.setShippingAddress(addr);
        em.flush();
        return cart;
    }

    @Test
    void testSaveAndRetrieveCart() {
        CatalogCart cart = createAndPersistCart();
        em.clear();

        var retrieved = checkoutService.getCart(cart.getId());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getTotal()).isEqualByComparingTo("53.57");
    }

    @Test
    void testCheckoutConvertsCartToOrder() {
        CatalogCart cart = createAndPersistCart();
        em.clear();

        CatalogOrder order = checkoutService.checkout(cart.getId(), "john@example.com", "John", "Doe");

        assertThat(order).isNotNull();
        assertThat(order.getOrderNumber()).isNotBlank();
        assertThat(order.getCurrency()).isEqualTo("USD");
    }

    @Test
    void testCheckoutDeletesCart() {
        CatalogCart cart = createAndPersistCart();
        UUID cartId = cart.getId();
        em.clear();

        checkoutService.checkout(cartId, "john@example.com", "John", "Doe");

        var deletedCart = checkoutService.getCart(cartId);
        assertThat(deletedCart).isEmpty();
    }

    @Test
    void testCheckoutWithMissingCartThrowsError() {
        UUID fakeId = UUID.randomUUID();
        assertThatThrownBy(() -> checkoutService.checkout(fakeId, "a@b.com", "A", "B"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("ERROR")
            .hasMessageContaining("Cart not found");
    }

    @Test
    void testOrderRetrievableAfterCheckout() {
        CatalogCart cart = createAndPersistCart();
        em.clear();

        CatalogOrder order = checkoutService.checkout(cart.getId(), "john@example.com", "John", "Doe");
        UUID orderId = order.getId();
        em.clear();

        var found = checkoutService.getOrder(orderId);
        assertThat(found).isPresent();
        assertThat(found.get().getOrderNumber()).isEqualTo(order.getOrderNumber());
    }
}
