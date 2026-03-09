package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CartTest {

    @Autowired
    private TestEntityManager em;

    @Test
    void testCreateCartWithLineItems() {
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

        em.persistAndFlush(cart);
        em.clear();

        CatalogCart reloaded = em.find(CatalogCart.class, cart.getId());
        assertThat(reloaded.getLineItems()).hasSize(2);
    }

    @Test
    void testCartWithAddress() {
        CatalogCart cart = new CatalogCart();
        cart = em.persistAndFlush(cart);

        CartAddress addr = new CartAddress();
        addr.setCart(cart);
        addr.setRecipientFirstName("John");
        addr.setRecipientLastName("Doe");
        addr.setAddressLine1("123 Main St");
        addr.setCity("Anytown");
        addr.setState("CA");
        addr.setPostalCode("90210");
        addr.setCountry("US");
        addr = em.persistAndFlush(addr);

        cart.setShippingAddress(addr);
        em.persistAndFlush(cart);
        em.clear();

        CatalogCart reloaded = em.find(CatalogCart.class, cart.getId());
        assertThat(reloaded.getShippingAddress()).isNotNull();
        assertThat(reloaded.getShippingAddress().getCity()).isEqualTo("Anytown");
    }

    @Test
    void testCartWithCreditCard() {
        CatalogCart cart = new CatalogCart();
        cart = em.persistAndFlush(cart);

        CartCreditCard cc = new CartCreditCard();
        cc.setCart(cart);
        cc.setNumber("411111******1111");
        cc.setVendor("Visa");
        cc.setName("John Doe");
        cc.setExpMonth(12);
        cc.setExpYear(2028);
        cc = em.persistAndFlush(cc);

        cart.setCreditCard(cc);
        em.persistAndFlush(cart);
        em.clear();

        CatalogCart reloaded = em.find(CatalogCart.class, cart.getId());
        assertThat(reloaded.getCreditCard()).isNotNull();
        assertThat(reloaded.getCreditCard().getVendor()).isEqualTo("Visa");
    }

    @Test
    void testCartMonetaryFields() {
        CatalogCart cart = new CatalogCart();
        cart.setSubTotal(new BigDecimal("49.98"));
        cart.setShippingCosts(new BigDecimal("5.99"));
        cart.setTaxRate(new BigDecimal("0.0725"));
        cart.setTotalTax(new BigDecimal("3.62"));
        cart.setTotal(new BigDecimal("59.59"));
        cart = em.persistAndFlush(cart);

        em.clear();

        CatalogCart reloaded = em.find(CatalogCart.class, cart.getId());
        assertThat(reloaded.getSubTotal()).isEqualByComparingTo(new BigDecimal("49.98"));
        assertThat(reloaded.getTotal()).isEqualByComparingTo(new BigDecimal("59.59"));
    }
}
