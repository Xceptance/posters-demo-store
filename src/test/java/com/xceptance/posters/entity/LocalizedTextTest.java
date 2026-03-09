package com.xceptance.posters.entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LocalizedTextTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testCreateLocalizedText() {
        Locale locale = new Locale();
        locale.setLocale("fr_FR");
        Locale savedLocale = entityManager.persistAndFlush(locale);

        LocalizedText localizedText = new LocalizedText();
        localizedText.setTextId(1);
        localizedText.setLocale(savedLocale);
        localizedText.setText("Bonjour");

        LocalizedText savedText = entityManager.persistAndFlush(localizedText);

        assertThat(savedText.getTextId()).isEqualTo(1);
        assertThat(savedText.getLocale().getId()).isEqualTo(savedLocale.getId());
        assertThat(savedText.getText()).isEqualTo("Bonjour");
    }

    @Test
    void testCompositeKeyUniqueness() {
        Locale locale1 = new Locale();
        locale1.setLocale("en_US");
        entityManager.persist(locale1);

        Locale locale2 = new Locale();
        locale2.setLocale("es_ES");
        entityManager.persistAndFlush(locale2);

        // Same textId, different locale -> Should be allowed
        LocalizedText text1 = new LocalizedText();
        text1.setTextId(100);
        text1.setLocale(locale1);
        text1.setText("Hello");
        entityManager.persistAndFlush(text1);

        LocalizedText text2 = new LocalizedText();
        text2.setTextId(100);
        text2.setLocale(locale2);
        text2.setText("Hola");
        
        LocalizedText savedText2 = entityManager.persistAndFlush(text2);
        assertThat(savedText2.getText()).isEqualTo("Hola");
    }
}
