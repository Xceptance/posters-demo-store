package com.xceptance.posters.service;
import com.xceptance.posters.util.CreditCardValidator;
import com.xceptance.posters.entity.CatalogOrder;
import com.xceptance.posters.entity.CatalogCart;
import com.xceptance.posters.entity.CartLineItem;
import com.xceptance.posters.entity.CartAddress;

import com.xceptance.posters.entity.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.Collections;
import com.xceptance.posters.dto.CartDto;
import com.xceptance.posters.dto.OrderDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test for CheckoutService — verifies cart management and checkout flow.
 */
@DataJpaTest
@Import({CheckoutService.class, CreditCardValidator.class})
class CheckoutServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private CheckoutService checkoutService;
    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private com.xceptance.posters.jmx.OrderProcessingMetrics orderProcessingMetrics;

    private CatalogCart createAndPersistCart() {
        CatalogCart cart = new CatalogCart();
        cart.setSubTotal(new BigDecimal("49.95"));
        cart.setTotalTax(new BigDecimal("3.62"));
        cart.setTotal(new BigDecimal("53.57"));
        em.persist(cart);
        em.flush();
        
        Mockito.when(cartService.toCartDto(Mockito.any(), Mockito.anyString(), Mockito.anyString()))
            .thenReturn(new CartDto(Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0));

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

    @Test
    void testToOrderDto() {
        CatalogCart cart = createAndPersistCart();
        em.clear();

        CatalogOrder order = checkoutService.checkout(cart.getId(), "test@user.com", "Test", "User");
        OrderDto dto = checkoutService.toOrderDto(order);

        assertThat(dto).isNotNull();
        assertThat(dto.orderNumber()).isEqualTo(order.getOrderNumber());
        assertThat(dto.lineItems()).hasSize(1);
        assertThat(dto.lineItems().get(0).sku()).isEqualTo("TEST-0001");
        assertThat(dto.lineItems().get(0).quantity()).isEqualTo(2);
    }

    @Test
    void testCheckoutWithEmptyCartThrowsError() {
        CatalogCart cart = new CatalogCart(); // Note: no line items added
        em.persist(cart);
        em.flush();
        UUID cartId = cart.getId();
        em.clear();

        assertThatThrownBy(() -> checkoutService.checkout(cartId, "a@b.com", "A", "B"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot place an order for an empty cart");
    }

    @Test
    void testCheckoutWithNullCartIdThrowsError() {
        assertThatThrownBy(() -> checkoutService.checkout(null, "a@b.com", "A", "B"))
            .isInstanceOf(org.springframework.dao.InvalidDataAccessApiUsageException.class)
            .hasMessageContaining("The given id must not be null");
    }
}
