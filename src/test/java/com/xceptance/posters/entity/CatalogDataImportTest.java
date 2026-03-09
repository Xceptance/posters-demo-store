package com.xceptance.posters.entity;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Integration test: XML parse → CatalogDataLoader → verify DB records.
 */
@DataJpaTest
@Import(CatalogDataLoader.class)
class CatalogDataImportTest {

    @Autowired
    private CatalogDataLoader loader;

    @Autowired
    private EntityManager em;

    private CatalogImportParser.CatalogImport parseTestXml() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("data/catalog-import-test.xml");
        assertThat(is).isNotNull();
        return new CatalogImportParser().parse(is);
    }

    @Test
    void testFullImportCreatesLocales() {
        var data = parseTestXml();
        loader.load(data);

        var locales = em.createQuery("SELECT l FROM Locale l", Locale.class).getResultList();
        assertThat(locales).hasSizeGreaterThanOrEqualTo(4);

        var codes = locales.stream().map(Locale::getLocale).toList();
        assertThat(codes).contains("en-US", "de-DE", "en-GB", "sv-SE");
    }

    @Test
    void testFullImportCreatesShippingMethods() {
        var data = parseTestXml();
        loader.load(data);

        var methods = em.createQuery("SELECT s FROM ShippingMethod s", ShippingMethod.class).getResultList();
        assertThat(methods).hasSizeGreaterThanOrEqualTo(2);
        assertThat(methods.stream().map(ShippingMethod::getSku).toList())
            .contains("SHIP-STD", "SHIP-EXP");
    }

    @Test
    void testFullImportCreatesSites() {
        var data = parseTestXml();
        loader.load(data);

        var sites = em.createQuery("SELECT s FROM Site s", Site.class).getResultList();
        assertThat(sites).hasSizeGreaterThanOrEqualTo(2);

        var usStore = sites.stream().filter(s -> s.getName().equals("US Store")).findFirst();
        assertThat(usStore).isPresent();
        assertThat(usStore.get().getCurrency()).isEqualTo("USD");
        assertThat(usStore.get().getPricesAreNet()).isTrue();
    }

    @Test
    void testFullImportCreatesCategories() {
        var data = parseTestXml();
        loader.load(data);

        var categories = em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
        // 2 top + 4 sub = 6 minimum
        assertThat(categories).hasSizeGreaterThanOrEqualTo(6);

        // Check subcategories have parents
        var subs = categories.stream().filter(c -> c.getParent() != null).toList();
        assertThat(subs).hasSizeGreaterThanOrEqualTo(4);
    }

    @Test
    void testFullImportCreatesProducts() {
        var data = parseTestXml();
        loader.load(data);

        var products = em.createQuery("SELECT p FROM CatalogProduct p", Product.class).getResultList();
        assertThat(products).hasSizeGreaterThanOrEqualTo(1);

        var grizzly = products.stream()
            .filter(p -> p.getSku().startsWith("GRBEAR"))
            .findFirst();
        assertThat(grizzly).isPresent();
        assertThat(grizzly.get().getSmallImageUrl()).contains("grizzly-bear");
    }

    @Test
    void testFullImportCreatesVariants() {
        var data = parseTestXml();
        loader.load(data);

        var variants = em.createQuery("SELECT v FROM Variant v", Variant.class).getResultList();
        assertThat(variants).hasSizeGreaterThanOrEqualTo(2);

        var fullSkus = variants.stream().map(Variant::getFullSku).toList();
        assertThat(fullSkus).contains("GRBEAR-0001", "GRBEAR-0002");
    }

    @Test
    void testImportWithBadCategoryThrows() {
        // Create data with a product referencing a non-existent sub-category
        var data = new CatalogImportParser.CatalogImport(
            List.of(new CatalogImportParser.LocaleEntry("en-US")),
            List.of(),
            List.of(),
            List.of(), // no categories
            List.of(new CatalogImportParser.ProductEntry(
                "NonExistent", false,
                java.util.Map.of("en-US", "Bad Product"),
                java.util.Map.of(), java.util.Map.of(),
                null, java.util.Map.of(), List.of()
            )),
            List.of()
        );

        assertThatThrownBy(() -> loader.load(data))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("ERROR")
            .hasMessageContaining("unknown sub-category");
    }
}
