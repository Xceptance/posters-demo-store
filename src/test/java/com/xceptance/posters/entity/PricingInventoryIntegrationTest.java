package com.xceptance.posters.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test: from Site → PriceTable → Prices, TaxTable → TaxRates,
 * and InventoryTable → InventoryEntries for a complete pricing/availability story.
 */
@DataJpaTest
class PricingInventoryIntegrationTest {

    @Autowired
    private TestEntityManager em;

    private Site site;
    private PriceTable priceTable;
    private TaxTable taxTable;
    private InventoryTable inventoryTable;

    @BeforeEach
    void setUp() {
        Locale locale = new Locale();
        locale.setLocale("en_US");
        locale = em.persistAndFlush(locale);

        priceTable = new PriceTable();
        priceTable.setName("US Standard");
        priceTable.setCurrency("USD");
        priceTable = em.persistAndFlush(priceTable);

        taxTable = new TaxTable();
        taxTable.setName("US Tax");
        taxTable = em.persistAndFlush(taxTable);

        site = new Site();
        site.setName("US Store");
        site.setMainLocale(locale);
        site.setCurrency("USD");
        site.setPriceTable(priceTable);
        site.setTaxTable(taxTable);
        site.setPricesAreNet(false);
        site = em.persistAndFlush(site);

        inventoryTable = new InventoryTable();
        inventoryTable.setSite(site);
        inventoryTable.setName("US Warehouse");
        inventoryTable = em.persistAndFlush(inventoryTable);

        // Add prices for 3 variants
        addPrice("POSTER01-0001", new BigDecimal("19.99"));
        addPrice("POSTER01-0002", new BigDecimal("29.99"));
        addPrice("POSTER01-0003", new BigDecimal("39.99"));

        // Add tax rate
        TaxRate salesTax = new TaxRate();
        salesTax.setTaxTable(taxTable);
        salesTax.setName("Sales Tax");
        salesTax.setRate(new BigDecimal("0.0725"));
        em.persistAndFlush(salesTax);

        // Add inventory
        addInventory("POSTER01-0001", 10);
        addInventory("POSTER01-0002", 0);  // out of stock
        // POSTER01-0003 — no entry → out of stock by convention

        em.flush();
        em.clear();
    }

    private void addPrice(String sku, BigDecimal amount) {
        Price p = new Price();
        p.setSku(sku);
        p.setPriceTable(priceTable);
        p.setPrice(amount);
        em.persist(p);
    }

    private void addInventory(String sku, int qty) {
        InventoryEntry ie = new InventoryEntry();
        ie.setInventoryTable(inventoryTable);
        ie.setSku(sku);
        ie.setAvailableQuantity(qty);
        em.persist(ie);
    }

    @Test
    void testSiteHasPriceTable() {
        Site reloaded = em.find(Site.class, site.getId());
        assertThat(reloaded.getPriceTable()).isNotNull();
        assertThat(reloaded.getPriceTable().getCurrency()).isEqualTo("USD");
    }

    @Test
    void testSiteHasTaxTable() {
        Site reloaded = em.find(Site.class, site.getId());
        assertThat(reloaded.getTaxTable()).isNotNull();
        assertThat(reloaded.getTaxTable().getName()).isEqualTo("US Tax");
    }

    @Test
    void testPriceTableHasPrices() {
        Price p1 = em.find(Price.class, new PriceId("POSTER01-0001", priceTable.getId()));
        Price p3 = em.find(Price.class, new PriceId("POSTER01-0003", priceTable.getId()));

        assertThat(p1).isNotNull();
        assertThat(p1.getPrice()).isEqualByComparingTo(new BigDecimal("19.99"));
        assertThat(p3.getPrice()).isEqualByComparingTo(new BigDecimal("39.99"));
    }

    @Test
    void testTaxRateRetrieval() {
        TaxRate rate = em.find(TaxRate.class, new TaxRateId(taxTable.getId(), "Sales Tax"));
        assertThat(rate).isNotNull();
        assertThat(rate.getRate()).isEqualByComparingTo(new BigDecimal("0.0725"));
    }

    @Test
    void testInStockItem() {
        InventoryEntry entry = em.find(InventoryEntry.class,
            new InventoryEntryId(inventoryTable.getId(), "POSTER01-0001"));
        assertThat(entry).isNotNull();
        assertThat(entry.getAvailableQuantity()).isEqualTo(10);
    }

    @Test
    void testExplicitlyZeroStock() {
        InventoryEntry entry = em.find(InventoryEntry.class,
            new InventoryEntryId(inventoryTable.getId(), "POSTER01-0002"));
        assertThat(entry).isNotNull();
        assertThat(entry.getAvailableQuantity()).isEqualTo(0);
    }

    @Test
    void testMissingEntryMeansOutOfStock() {
        InventoryEntry entry = em.find(InventoryEntry.class,
            new InventoryEntryId(inventoryTable.getId(), "POSTER01-0003"));
        assertThat(entry).isNull();
    }

    @Test
    void testGrossPrice() {
        Site reloaded = em.find(Site.class, site.getId());
        assertThat(reloaded.getPricesAreNet()).isFalse();  // prices include tax
    }
}
