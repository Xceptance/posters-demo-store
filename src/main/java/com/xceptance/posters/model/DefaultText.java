package com.xceptance.posters.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Stores a default (original language) text and its translations.
 */
@Entity
@Table(name = "default_text")
public class DefaultText
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 4096)
    private String originalText;

    @ManyToOne
    private Language originalLanguage;

    @OneToMany(mappedBy = "originalText", fetch = FetchType.EAGER)
    private List<Translation> translations = new ArrayList<>();

    public DefaultText()
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

    public String getOriginalText()
    {
        return originalText;
    }

    public void setOriginalText(String originalText)
    {
        this.originalText = originalText;
    }

    public Language getOriginalLanguage()
    {
        return originalLanguage;
    }

    public void setOriginalLanguage(Language originalLanguage)
    {
        this.originalLanguage = originalLanguage;
    }

    public List<Translation> getTranslations()
    {
        return translations;
    }

    public void setTranslations(List<Translation> translations)
    {
        this.translations = translations;
    }

    public void addTranslation(Translation translation)
    {
        this.translations.add(translation);
    }

    /**
     * Returns the translated text for the given language code (e.g. "de-DE"),
     * or falls back to the original text if no matching translation is found.
     * Checks both Language.code (base, e.g. "de") and Language.fallbackCode
     * (full, e.g. "de-DE") for a match.
     */
    public String getText(String langCode)
    {
        if (langCode != null && translations != null)
        {
            for (Translation t : translations)
            {
                Language lang = t.getTranslationLanguage();
                if (lang != null
                    && (langCode.equals(lang.getCode())
                        || langCode.equals(lang.getFallbackCode())))
                {
                    return t.getTranslationText();
                }
            }
        }
        return originalText;
    }
}
