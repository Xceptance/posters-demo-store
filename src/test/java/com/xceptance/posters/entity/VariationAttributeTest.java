package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VariationAttributeTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testCreateVariationAttribute() {
        VariationAttribute attr = new VariationAttribute();
        attr.setName("Color");

        VariationAttribute saved = entityManager.persistAndFlush(attr);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Color");
    }

    @Test
    void testCreateVariationAttributeValue() {
        VariationAttribute attr = new VariationAttribute();
        attr.setName("Size");
        attr = entityManager.persistAndFlush(attr);

        VariationAttributeValue val = new VariationAttributeValue();
        val.setAttribute(attr);
        val.setValue("16×20");

        VariationAttributeValue saved = entityManager.persistAndFlush(val);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getValue()).isEqualTo("16×20");
        assertThat(saved.getAttribute().getName()).isEqualTo("Size");
    }

    @Test
    void testAttributeHasMultipleValues() {
        VariationAttribute attr = new VariationAttribute();
        attr.setName("Finish");
        attr = entityManager.persistAndFlush(attr);

        VariationAttributeValue matte = new VariationAttributeValue();
        matte.setAttribute(attr);
        matte.setValue("matte");
        entityManager.persist(matte);

        VariationAttributeValue glossy = new VariationAttributeValue();
        glossy.setAttribute(attr);
        glossy.setValue("glossy");
        entityManager.persistAndFlush(glossy);

        entityManager.clear();

        VariationAttribute reloaded = entityManager.find(VariationAttribute.class, attr.getId());
        assertThat(reloaded.getValues()).hasSize(2);
    }
}
