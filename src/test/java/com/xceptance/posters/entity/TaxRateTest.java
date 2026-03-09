package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaxRateTest {

    @Autowired
    private TestEntityManager em;

    @Test
    void testCreateTaxTable() {
        TaxTable tt = new TaxTable();
        tt.setName("Germany Standard");
        tt.setDescription("German tax rates");
        TaxTable saved = em.persistAndFlush(tt);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Germany Standard");
    }

    @Test
    void testCreateTaxRate() {
        TaxTable tt = new TaxTable();
        tt.setName("US Default");
        tt = em.persistAndFlush(tt);

        TaxRate rate = new TaxRate();
        rate.setTaxTable(tt);
        rate.setName("Standard Rate");
        rate.setRate(new BigDecimal("0.0725"));
        em.persistAndFlush(rate);

        assertThat(rate.getRate()).isEqualByComparingTo(new BigDecimal("0.0725"));
    }

    @Test
    void testMultipleRatesPerTable() {
        TaxTable tt = new TaxTable();
        tt.setName("Germany");
        tt = em.persistAndFlush(tt);

        TaxRate standard = new TaxRate();
        standard.setTaxTable(tt);
        standard.setName("Standard Rate");
        standard.setRate(new BigDecimal("0.1900"));
        em.persist(standard);

        TaxRate reduced = new TaxRate();
        reduced.setTaxTable(tt);
        reduced.setName("Reduced Rate");
        reduced.setRate(new BigDecimal("0.0700"));
        em.persistAndFlush(reduced);

        assertThat(standard.getRate()).isEqualByComparingTo(new BigDecimal("0.19"));
        assertThat(reduced.getRate()).isEqualByComparingTo(new BigDecimal("0.07"));
    }
}
