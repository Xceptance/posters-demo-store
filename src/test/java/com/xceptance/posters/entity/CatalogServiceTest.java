package com.xceptance.posters.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for CatalogService — verifies category navigation,
 * product listing, and variant retrieval through the new entity model.
 */
@DataJpaTest
@Import(CatalogService.class)
class CatalogServiceTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private CatalogService catalogService;

    private Category topCat;
    private Category subCat1;
    private Category subCat2;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        // Create category hierarchy
        topCat = new Category();
        topCat.setNameTextId(9001);
        topCat = em.persist(topCat);

        subCat1 = new Category();
        subCat1.setNameTextId(9002);
        subCat1.setParent(topCat);
        subCat1 = em.persist(subCat1);

        subCat2 = new Category();
        subCat2.setNameTextId(9003);
        subCat2.setParent(topCat);
        subCat2 = em.persist(subCat2);

        // Create products in subcategories
        product1 = new Product();
        product1.setSku("CATTEST01");
        product1.getCategories().add(subCat1);
        product1 = em.persist(product1);

        product2 = new Product();
        product2.setSku("CATTEST02");
        product2.getCategories().add(subCat1);
        product2 = em.persist(product2);

        // Create variants
        Variant v1 = new Variant();
        v1.setProduct(product1);
        v1.setVariantNumber(1);
        em.persist(v1);

        Variant v2 = new Variant();
        v2.setProduct(product1);
        v2.setVariantNumber(2);
        em.persist(v2);

        em.flush();
    }

    @Test
    void testGetTopCategories() {
        List<Category> topCats = catalogService.getTopCategories();
        assertThat(topCats).isNotEmpty();
        assertThat(topCats).anyMatch(c -> c.getId().equals(topCat.getId()));
    }

    @Test
    void testGetSubCategories() {
        List<Category> subs = catalogService.getSubCategories(topCat.getId());
        assertThat(subs).hasSize(2);
    }

    @Test
    void testGetCategoryById() {
        var cat = catalogService.getCategoryById(subCat1.getId());
        assertThat(cat).isPresent();
        assertThat(cat.get().getParent()).isNotNull();
    }

    @Test
    void testGetProductsByCategory() {
        List<Product> products = catalogService.getProductsByCategory(subCat1.getId());
        assertThat(products).hasSize(2);
        assertThat(products.stream().map(Product::getSku).toList())
            .containsExactlyInAnyOrder("CATTEST01", "CATTEST02");
    }

    @Test
    void testGetProductBySku() {
        var product = catalogService.getProductBySku("CATTEST01");
        assertThat(product).isPresent();
        assertThat(product.get().getId()).isEqualTo(product1.getId());
    }

    @Test
    void testGetVariantsByProduct() {
        List<Variant> variants = catalogService.getVariantsByProduct(product1.getId());
        assertThat(variants).hasSize(2);
        assertThat(variants.get(0).getFullSku()).isEqualTo("CATTEST01-0001");
        assertThat(variants.get(1).getFullSku()).isEqualTo("CATTEST01-0002");
    }

    @Test
    void testEmptyCategoryReturnsNoProducts() {
        List<Product> products = catalogService.getProductsByCategory(subCat2.getId());
        assertThat(products).isEmpty();
    }
}
