package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PricingTest {

    @Autowired
    private TestEntityManager em;

    @Test
    void testCreatePriceTable() {
        PriceTable pt = new PriceTable();
        pt.setName("US Standard");
        pt.setDescription("Default US pricing");
        pt.setCurrency("USD");

        PriceTable saved = em.persistAndFlush(pt);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCurrency()).isEqualTo("USD");
    }

    @Test
    void testCreatePrice() {
        PriceTable pt = new PriceTable();
        pt.setName("EU Standard");
        pt.setDescription("Default EU pricing");
        pt.setCurrency("EUR");
        pt = em.persistAndFlush(pt);

        Price price = new Price();
        price.setSku("POSTER01-0001");
        price.setPriceTable(pt);
        price.setPrice(new BigDecimal("29.99"));

        Price saved = em.persistAndFlush(price);

        assertThat(saved.getSku()).isEqualTo("POSTER01-0001");
        assertThat(saved.getPrice()).isEqualByComparingTo(new BigDecimal("29.99"));
        assertThat(saved.getPriceTable().getCurrency()).isEqualTo("EUR");
    }

    @Test
    void testCompositeKeyUniqueness() {
        PriceTable pt1 = new PriceTable();
        pt1.setName("US");
        pt1.setCurrency("USD");
        pt1 = em.persistAndFlush(pt1);

        PriceTable pt2 = new PriceTable();
        pt2.setName("EU");
        pt2.setCurrency("EUR");
        pt2 = em.persistAndFlush(pt2);

        // Same SKU, different price tables → allowed
        Price p1 = new Price();
        p1.setSku("POSTER01-0001");
        p1.setPriceTable(pt1);
        p1.setPrice(new BigDecimal("29.99"));
        em.persistAndFlush(p1);

        Price p2 = new Price();
        p2.setSku("POSTER01-0001");
        p2.setPriceTable(pt2);
        p2.setPrice(new BigDecimal("24.99"));
        em.persistAndFlush(p2);

        assertThat(p2.getPrice()).isEqualByComparingTo(new BigDecimal("24.99"));
    }

    @Test
    void testShippingMethodPrice() {
        PriceTable pt = new PriceTable();
        pt.setName("US");
        pt.setCurrency("USD");
        pt = em.persistAndFlush(pt);

        Price shippingPrice = new Price();
        shippingPrice.setSku("SHIP-STD");
        shippingPrice.setPriceTable(pt);
        shippingPrice.setPrice(new BigDecimal("7.00"));
        em.persistAndFlush(shippingPrice);

        assertThat(shippingPrice.getSku()).isEqualTo("SHIP-STD");
    }
}
