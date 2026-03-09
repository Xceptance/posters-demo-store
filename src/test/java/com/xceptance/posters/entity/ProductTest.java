package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testCreateProduct() {
        Product product = new Product();
        product.setSku("POSTER");
        product.setNameTextId(1);
        product.setDescriptionDetailTextId(2);
        product.setDescriptionOverviewTextId(3);
        product.setSmallImageUrl("/img/small.jpg");
        product.setMediumImageUrl("/img/medium.jpg");
        product.setLargeImageUrl("/img/large.jpg");
        product.setOriginalImageUrl("/img/original.jpg");
        product.setShowInCarousel(true);

        Product saved = entityManager.persistAndFlush(product);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSku()).isEqualTo("POSTER");
        assertThat(saved.getShowInCarousel()).isTrue();
    }

    @Test
    void testProductCategoryRelationship() {
        Category cat = new Category();
        cat.setNameTextId(100);
        cat = entityManager.persistAndFlush(cat);

        Product product = new Product();
        product.setSku("PRD001");
        product.setNameTextId(200);
        product.getCategories().add(cat);
        product = entityManager.persistAndFlush(product);

        entityManager.clear();

        Product reloaded = entityManager.find(Product.class, product.getId());
        assertThat(reloaded.getCategories()).hasSize(1);
    }

    @Test
    void testProductVariationAttributeRelationship() {
        VariationAttribute colorAttr = new VariationAttribute();
        colorAttr.setName("Color");
        colorAttr = entityManager.persistAndFlush(colorAttr);

        Product product = new Product();
        product.setSku("PRD002");
        product.setNameTextId(300);
        product.getVariationAttributes().add(colorAttr);
        product = entityManager.persistAndFlush(product);

        entityManager.clear();

        Product reloaded = entityManager.find(Product.class, product.getId());
        assertThat(reloaded.getVariationAttributes()).hasSize(1);
        assertThat(reloaded.getVariationAttributes().iterator().next().getName()).isEqualTo("Color");
    }
}
