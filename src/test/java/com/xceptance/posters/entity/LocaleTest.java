package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class LocaleTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testCreateLocale() {
        Locale locale = new Locale();
        locale.setLocale("en_US");
        
        Locale saved = entityManager.persistAndFlush(locale);
        
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getLocale()).isEqualTo("en_US");
    }

    @Test
    void testLocaleIsUnique() {
        Locale locale1 = new Locale();
        locale1.setLocale("de_DE");
        entityManager.persistAndFlush(locale1);

        Locale locale2 = new Locale();
        locale2.setLocale("de_DE");

        assertThatThrownBy(() -> entityManager.persistAndFlush(locale2))
            .isInstanceOf(Exception.class); // Depending on specific constraint violation wrapper
    }
}
