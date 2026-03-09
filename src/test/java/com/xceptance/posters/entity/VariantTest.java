package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VariantTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testCreateVariant() {
        Product product = new Product();
        product.setSku("POSTER01");
        product.setNameTextId(1);
        product = entityManager.persistAndFlush(product);

        Variant variant = new Variant();
        variant.setProduct(product);
        variant.setVariantNumber(1);
        variant = entityManager.persistAndFlush(variant);

        assertThat(variant.getId()).isNotNull();
        assertThat(variant.getFullSku()).isEqualTo("POSTER01-0001");
    }

    @Test
    void testVariantWithAttributeValues() {
        Product product = new Product();
        product.setSku("POSTER02");
        product.setNameTextId(10);
        product = entityManager.persistAndFlush(product);

        VariationAttribute colorAttr = new VariationAttribute();
        colorAttr.setName("Color");
        colorAttr = entityManager.persistAndFlush(colorAttr);

        VariationAttributeValue red = new VariationAttributeValue();
        red.setAttribute(colorAttr);
        red.setValue("red");
        red = entityManager.persistAndFlush(red);

        Variant variant = new Variant();
        variant.setProduct(product);
        variant.setVariantNumber(1);
        variant.getAttributeValues().add(red);
        variant = entityManager.persistAndFlush(variant);

        entityManager.clear();

        Variant reloaded = entityManager.find(Variant.class, variant.getId());
        assertThat(reloaded.getAttributeValues()).hasSize(1);
        assertThat(reloaded.getAttributeValues().iterator().next().getValue()).isEqualTo("red");
        assertThat(reloaded.getFullSku()).isEqualTo("POSTER02-0001");
    }

    @Test
    void testProductHasVariants() {
        Product product = new Product();
        product.setSku("POSTER03");
        product.setNameTextId(20);
        product = entityManager.persistAndFlush(product);

        Variant v1 = new Variant();
        v1.setProduct(product);
        v1.setVariantNumber(1);
        entityManager.persist(v1);

        Variant v2 = new Variant();
        v2.setProduct(product);
        v2.setVariantNumber(2);
        entityManager.persistAndFlush(v2);

        entityManager.clear();

        Product reloaded = entityManager.find(Product.class, product.getId());
        assertThat(reloaded.getVariants()).hasSize(2);
    }
}
