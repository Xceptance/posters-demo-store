package com.xceptance.posters.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for CatalogImportParser — pure Java, no Spring context.
 */
class CatalogImportParserTest {

    private CatalogImportParser parser;

    @BeforeEach
    void setUp() {
        parser = new CatalogImportParser();
    }

    private CatalogImportParser.CatalogImport parseTestFile() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("data/catalog-import-test.xml");
        assertThat(is).as("Test XML file must be on classpath").isNotNull();
        return parser.parse(is);
    }

    @Test
    void testParseLocales() {
        var result = parseTestFile();
        assertThat(result.locales()).hasSize(4);
        assertThat(result.locales().get(0).code()).isEqualTo("en-US");
        assertThat(result.locales().get(1).code()).isEqualTo("de-DE");
    }

    @Test
    void testParseShippingMethods() {
        var result = parseTestFile();
        assertThat(result.shippingMethods()).hasSize(2);
        assertThat(result.shippingMethods().get(0).sku()).isEqualTo("SHIP-STD");
        assertThat(result.shippingMethods().get(0).names()).containsEntry("en-US", "Standard Shipping");
    }

    @Test
    void testParseSites() {
        var result = parseTestFile();
        assertThat(result.sites()).hasSize(2);

        var usSite = result.sites().get(0);
        assertThat(usSite.name()).isEqualTo("US Store");
        assertThat(usSite.currency()).isEqualTo("USD");
        assertThat(usSite.pricesAreNet()).isTrue();
        assertThat(usSite.mainLocale()).isEqualTo("en-US");
        assertThat(usSite.localeCodes()).containsExactly("en-US", "de-DE", "sv-SE");
        assertThat(usSite.shippingRefs()).hasSize(2);
    }

    @Test
    void testParseCategories() {
        var result = parseTestFile();
        assertThat(result.categories()).hasSize(2);

        var nature = result.categories().get(0);
        assertThat(nature.names()).containsEntry("en-US", "World of Nature");
        assertThat(nature.subCategories()).hasSize(3);
        assertThat(nature.subCategories().get(0).names()).containsEntry("en-US", "Animals");
    }

    @Test
    void testParseProducts() {
        var result = parseTestFile();
        assertThat(result.products()).hasSize(1);

        var product = result.products().get(0);
        assertThat(product.subCategory()).isEqualTo("Animals");
        assertThat(product.names()).containsEntry("en-US", "Grizzly Bear");
        assertThat(product.names()).containsEntry("de-DE", "Grizzlybär");
        assertThat(product.images()).isNotNull();
        assertThat(product.images().small()).contains("grizzly-bear-small");
    }

    @Test
    void testParseVariants() {
        var result = parseTestFile();
        var product = result.products().get(0);
        assertThat(product.variants()).hasSize(2);

        var v1 = product.variants().get(0);
        assertThat(v1.sku()).isEqualTo("GRBEAR-0001");
        assertThat(v1.attributes()).containsEntry("size", "16x12");
        assertThat(v1.prices()).containsEntry("USD", "17.00");
        assertThat(v1.inventoryQuantity()).isEqualTo(100);
    }

    @Test
    void testParseTaxTables() {
        var result = parseTestFile();
        assertThat(result.taxTables()).hasSize(1);
        assertThat(result.taxTables().get(0).name()).isEqualTo("US Tax");
        assertThat(result.taxTables().get(0).rates()).containsEntry("GRBEAR-0001", "0.0725");
    }

    @Test
    void testErrorOnMissingAttribute() {
        String badXml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <catalog-import>
              <locales><locale/></locales>
              <shipping-methods/>
              <sites/>
              <categories/>
              <products/>
              <tax-tables/>
            </catalog-import>
            """;
        InputStream is = new java.io.ByteArrayInputStream(badXml.getBytes());
        assertThatThrownBy(() -> parser.parse(is))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("missing");
    }
}
