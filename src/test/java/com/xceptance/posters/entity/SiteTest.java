package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SiteTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testCreateSite() {
        // Create required locales
        Locale mainLocale = new Locale();
        mainLocale.setLocale("en_US");
        mainLocale = entityManager.persistAndFlush(mainLocale);

        Locale fallbackLocale = new Locale();
        fallbackLocale.setLocale("en_GB");
        fallbackLocale = entityManager.persistAndFlush(fallbackLocale);

        // Create price and tax tables
        PriceTable pt = new PriceTable();
        pt.setName("US Standard");
        pt.setCurrency("USD");
        pt = entityManager.persistAndFlush(pt);

        TaxTable tt = new TaxTable();
        tt.setName("US Tax");
        tt = entityManager.persistAndFlush(tt);

        // Create the site
        Site site = new Site();
        site.setName("United States");
        site.setDescription("US Demo Store");
        site.setMainLocale(mainLocale);
        site.setFallbackLocale(fallbackLocale);
        site.setCurrency("USD");
        site.setPriceTable(pt);
        site.setTaxTable(tt);
        site.setPricesAreNet(true);

        Site savedSite = entityManager.persistAndFlush(site);

        assertThat(savedSite.getId()).isNotNull();
        assertThat(savedSite.getName()).isEqualTo("United States");
        assertThat(savedSite.getMainLocale().getLocale()).isEqualTo("en_US");
        assertThat(savedSite.getFallbackLocale().getLocale()).isEqualTo("en_GB");
        assertThat(savedSite.getCurrency()).isEqualTo("USD");
        assertThat(savedSite.getPriceTable().getName()).isEqualTo("US Standard");
        assertThat(savedSite.getTaxTable().getName()).isEqualTo("US Tax");
        assertThat(savedSite.getPricesAreNet()).isTrue();
    }
}
