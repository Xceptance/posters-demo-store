package com.xceptance.posters.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xceptance.posters.model.DefaultText;
import com.xceptance.posters.model.Language;
import com.xceptance.posters.model.Translation;

public interface TranslationRepository extends JpaRepository<Translation, Integer>
{
    Optional<Translation> findByOriginalTextAndTranslationLanguage(DefaultText originalText, Language language);
}
