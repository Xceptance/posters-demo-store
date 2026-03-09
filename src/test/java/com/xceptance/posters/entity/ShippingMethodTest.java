package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ShippingMethodTest {

    @Autowired
    private TestEntityManager em;

    private Site createSite(String name) {
        Locale locale = new Locale();
        locale.setLocale(name + "_locale");
        locale = em.persistAndFlush(locale);

        PriceTable pt = new PriceTable();
        pt.setName(name + " Prices");
        pt.setCurrency("USD");
        pt = em.persistAndFlush(pt);

        TaxTable tt = new TaxTable();
        tt.setName(name + " Tax");
        tt = em.persistAndFlush(tt);

        Site site = new Site();
        site.setName(name);
        site.setMainLocale(locale);
        site.setCurrency("USD");
        site.setPriceTable(pt);
        site.setTaxTable(tt);
        site.setPricesAreNet(true);
        return em.persistAndFlush(site);
    }

    @Test
    void testCreateShippingMethod() {
        ShippingMethod sm = new ShippingMethod();
        sm.setSku("SHIP-STD");
        sm = em.persistAndFlush(sm);

        assertThat(sm.getId()).isNotNull();
        assertThat(sm.getSku()).isEqualTo("SHIP-STD");
    }

    @Test
    void testSiteShippingMethodMapping() {
        Site site = createSite("US Store");

        ShippingMethod standard = new ShippingMethod();
        standard.setSku("SHIP-STD");
        standard = em.persistAndFlush(standard);

        ShippingMethod express = new ShippingMethod();
        express.setSku("SHIP-EXP");
        express = em.persistAndFlush(express);

        SiteShippingMethod ssm1 = new SiteShippingMethod();
        ssm1.setSite(site);
        ssm1.setShippingMethod(standard);
        ssm1.setActive(true);
        em.persist(ssm1);

        SiteShippingMethod ssm2 = new SiteShippingMethod();
        ssm2.setSite(site);
        ssm2.setShippingMethod(express);
        ssm2.setActive(false);
        em.persistAndFlush(ssm2);

        em.clear();

        SiteShippingMethod reloaded = em.find(SiteShippingMethod.class,
            new SiteShippingMethodId(site.getId(), standard.getId()));
        assertThat(reloaded).isNotNull();
        assertThat(reloaded.getActive()).isTrue();

        SiteShippingMethod inactive = em.find(SiteShippingMethod.class,
            new SiteShippingMethodId(site.getId(), express.getId()));
        assertThat(inactive).isNotNull();
        assertThat(inactive.getActive()).isFalse();
    }
}
