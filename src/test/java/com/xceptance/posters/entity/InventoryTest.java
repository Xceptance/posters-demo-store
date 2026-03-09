package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class InventoryTest {

    @Autowired
    private TestEntityManager em;

    private Site createSite(String name, String currency) {
        Locale locale = new Locale();
        locale.setLocale(name + "_locale");
        locale = em.persistAndFlush(locale);

        PriceTable pt = new PriceTable();
        pt.setName(name + " Prices");
        pt.setCurrency(currency);
        pt = em.persistAndFlush(pt);

        TaxTable tt = new TaxTable();
        tt.setName(name + " Tax");
        tt = em.persistAndFlush(tt);

        Site site = new Site();
        site.setName(name);
        site.setMainLocale(locale);
        site.setCurrency(currency);
        site.setPriceTable(pt);
        site.setTaxTable(tt);
        site.setPricesAreNet(true);
        return em.persistAndFlush(site);
    }

    @Test
    void testCreateInventoryTable() {
        Site site = createSite("US", "USD");

        InventoryTable invTable = new InventoryTable();
        invTable.setSite(site);
        invTable.setName("US Warehouse");
        invTable = em.persistAndFlush(invTable);

        assertThat(invTable.getId()).isNotNull();
        assertThat(invTable.getSite().getName()).isEqualTo("US");
    }

    @Test
    void testCreateInventoryEntry() {
        Site site = createSite("DE", "EUR");

        InventoryTable invTable = new InventoryTable();
        invTable.setSite(site);
        invTable.setName("DE Warehouse");
        invTable = em.persistAndFlush(invTable);

        InventoryEntry entry = new InventoryEntry();
        entry.setInventoryTable(invTable);
        entry.setSku("POSTER01-0001");
        entry.setAvailableQuantity(42);
        em.persistAndFlush(entry);

        assertThat(entry.getAvailableQuantity()).isEqualTo(42);
    }

    @Test
    void testMissingSku_MeansOutOfStock() {
        Site site = createSite("UK", "GBP");

        InventoryTable invTable = new InventoryTable();
        invTable.setSite(site);
        invTable.setName("UK Warehouse");
        invTable = em.persistAndFlush(invTable);

        // No entry created for "POSTER99-0001" → out of stock by convention
        // This is a conceptual test — we verify by checking the entry doesn't exist
        em.clear();
        InventoryEntry notFound = em.find(InventoryEntry.class,
            new InventoryEntryId(invTable.getId(), "POSTER99-0001"));
        assertThat(notFound).isNull();
    }
}
