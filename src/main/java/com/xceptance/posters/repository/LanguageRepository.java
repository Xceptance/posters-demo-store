package com.xceptance.posters.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xceptance.posters.model.Language;

public interface LanguageRepository extends JpaRepository<Language, Integer>
{
    Optional<Language> findByCode(String code);
}
