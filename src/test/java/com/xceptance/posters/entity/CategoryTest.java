package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testCreateTopCategory() {
        Category top = new Category();
        top.setNameTextId(1);
        top.setDescriptionTextId(2);
        top.setOverviewTextId(3);

        Category saved = entityManager.persistAndFlush(top);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getParent()).isNull();
    }

    @Test
    void testCreateSubCategory() {
        Category top = new Category();
        top.setNameTextId(10);
        top.setDescriptionTextId(11);
        top.setOverviewTextId(12);
        top = entityManager.persistAndFlush(top);

        Category sub = new Category();
        sub.setParent(top);
        sub.setNameTextId(20);
        sub.setDescriptionTextId(21);
        sub.setOverviewTextId(22);
        sub = entityManager.persistAndFlush(sub);

        assertThat(sub.getParent()).isNotNull();
        assertThat(sub.getParent().getId()).isEqualTo(top.getId());
    }

    @Test
    void testTopCategoryHasChildren() {
        Category top = new Category();
        top.setNameTextId(30);
        top.setDescriptionTextId(31);
        top.setOverviewTextId(32);
        top = entityManager.persistAndFlush(top);

        Category sub1 = new Category();
        sub1.setParent(top);
        sub1.setNameTextId(40);
        sub1.setDescriptionTextId(41);
        sub1.setOverviewTextId(42);
        entityManager.persistAndFlush(sub1);

        Category sub2 = new Category();
        sub2.setParent(top);
        sub2.setNameTextId(50);
        sub2.setDescriptionTextId(51);
        sub2.setOverviewTextId(52);
        entityManager.persistAndFlush(sub2);

        entityManager.clear();

        Category reloaded = entityManager.find(Category.class, top.getId());
        assertThat(reloaded.getChildren()).hasSize(2);
    }
}
