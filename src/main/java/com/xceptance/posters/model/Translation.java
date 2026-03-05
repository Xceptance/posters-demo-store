package com.xceptance.posters.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * A translation of a {@link DefaultText} into a specific {@link Language}.
 */
@Entity
@Table(name = "translation")
public class Translation
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    private DefaultText originalText;

    @ManyToOne
    private Language translationLanguage;

    @Column(length = 4096)
    private String translationText;

    public Translation()
    {
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public DefaultText getOriginalText()
    {
        return originalText;
    }

    public void setOriginalText(DefaultText originalText)
    {
        this.originalText = originalText;
    }

    public Language getTranslationLanguage()
    {
        return translationLanguage;
    }

    public void setTranslationLanguage(Language translationLanguage)
    {
        this.translationLanguage = translationLanguage;
    }

    public String getTranslationText()
    {
        return translationText;
    }

    public void setTranslationText(String translationText)
    {
        this.translationText = translationText;
    }
}
