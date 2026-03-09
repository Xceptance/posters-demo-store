package com.xceptance.posters.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for the 3-tier product catalog navigation:
 * Category → Product → Variant (with VariationAttributeValues).
 */
@DataJpaTest
class CatalogNavigationTest {

    @Autowired
    private TestEntityManager em;

    private Category topCategory;
    private Category subCategory;
    private Product masterProduct;
    private Variant variant1;
    private Variant variant2;

    @BeforeEach
    void setUp() {
        // Build catalog: Top Category → Sub Category → Product → 2 Variants

        topCategory = new Category();
        topCategory.setNameTextId(1);
        topCategory = em.persistAndFlush(topCategory);

        subCategory = new Category();
        subCategory.setParent(topCategory);
        subCategory.setNameTextId(2);
        subCategory = em.persistAndFlush(subCategory);

        // Variation attributes: Size & Finish
        VariationAttribute sizeAttr = new VariationAttribute();
        sizeAttr.setName("Size");
        sizeAttr = em.persistAndFlush(sizeAttr);

        VariationAttribute finishAttr = new VariationAttribute();
        finishAttr.setName("Finish");
        finishAttr = em.persistAndFlush(finishAttr);

        VariationAttributeValue size16 = new VariationAttributeValue();
        size16.setAttribute(sizeAttr);
        size16.setValue("16×20");
        size16 = em.persistAndFlush(size16);

        VariationAttributeValue size24 = new VariationAttributeValue();
        size24.setAttribute(sizeAttr);
        size24.setValue("24×36");
        size24 = em.persistAndFlush(size24);

        VariationAttributeValue matte = new VariationAttributeValue();
        matte.setAttribute(finishAttr);
        matte.setValue("matte");
        matte = em.persistAndFlush(matte);

        // Master product (linked to sub-category)
        masterProduct = new Product();
        masterProduct.setSku("POSTER01");
        masterProduct.setNameTextId(100);
        masterProduct.setShowInCarousel(true);
        masterProduct.getCategories().add(subCategory);
        masterProduct.getVariationAttributes().add(sizeAttr);
        masterProduct.getVariationAttributes().add(finishAttr);
        masterProduct = em.persistAndFlush(masterProduct);

        // Variant 1: 16×20, matte
        variant1 = new Variant();
        variant1.setProduct(masterProduct);
        variant1.setVariantNumber(1);
        variant1.getAttributeValues().add(size16);
        variant1.getAttributeValues().add(matte);
        variant1 = em.persist(variant1);

        // Variant 2: 24×36, matte
        variant2 = new Variant();
        variant2.setProduct(masterProduct);
        variant2.setVariantNumber(2);
        variant2.getAttributeValues().add(size24);
        variant2.getAttributeValues().add(matte);
        variant2 = em.persist(variant2);

        em.flush();
        em.clear();
    }

    @Test
    void testNavigateFromTopCategory() {
        Category top = em.find(Category.class, topCategory.getId());
        assertThat(top.getChildren()).hasSize(1);

        Category sub = top.getChildren().get(0);
        assertThat(sub.getNameTextId()).isEqualTo(2);
    }

    @Test
    void testSubCategoryContainsProduct() {
        // Reload from DB
        Product product = em.find(Product.class, masterProduct.getId());
        assertThat(product.getCategories()).hasSize(1);

        Category cat = product.getCategories().iterator().next();
        assertThat(cat.getId()).isEqualTo(subCategory.getId());
    }

    @Test
    void testProductHasVariants() {
        Product product = em.find(Product.class, masterProduct.getId());
        assertThat(product.getVariants()).hasSize(2);
    }

    @Test
    void testVariantFullSku() {
        Variant v1 = em.find(Variant.class, variant1.getId());
        Variant v2 = em.find(Variant.class, variant2.getId());

        assertThat(v1.getFullSku()).isEqualTo("POSTER01-0001");
        assertThat(v2.getFullSku()).isEqualTo("POSTER01-0002");

        // Both full SKUs should be valid per the SkuValidator
        assertThat(SkuValidator.isValid(v1.getFullSku())).isTrue();
        assertThat(SkuValidator.isValid(v2.getFullSku())).isTrue();
    }

    @Test
    void testVariantAttributeValues() {
        Variant v1 = em.find(Variant.class, variant1.getId());
        assertThat(v1.getAttributeValues()).hasSize(2);

        // Should have "16×20" and "matte"
        var valueNames = v1.getAttributeValues().stream()
            .map(VariationAttributeValue::getValue)
            .toList();
        assertThat(valueNames).containsExactlyInAnyOrder("16×20", "matte");
    }

    @Test
    void testProductVariationAttributes() {
        Product product = em.find(Product.class, masterProduct.getId());
        assertThat(product.getVariationAttributes()).hasSize(2);

        var attrNames = product.getVariationAttributes().stream()
            .map(VariationAttribute::getName)
            .toList();
        assertThat(attrNames).containsExactlyInAnyOrder("Size", "Finish");
    }
}
