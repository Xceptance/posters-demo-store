package com.xceptance.posters.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * A supported language/locale in the application (e.g. en-US, de-DE).
 */
@Entity
@Table(name = "supported_language")
public class Language
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String languageGroup;
    private String preciseName;
    private String endonym;
    private String preciseEndonym;
    private String disambiguousEndonym;
    private String code;
    private String fallbackCode;

    @OneToMany(mappedBy = "translationLanguage")
    private List<Translation> translations = new ArrayList<>();

    @OneToMany(mappedBy = "originalLanguage")
    private List<DefaultText> originalTexts = new ArrayList<>();

    public Language()
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

    public String getLanguageGroup()
    {
        return languageGroup;
    }

    public void setLanguageGroup(String languageGroup)
    {
        this.languageGroup = languageGroup;
    }

    public String getPreciseName()
    {
        return preciseName;
    }

    public void setPreciseName(String preciseName)
    {
        this.preciseName = preciseName;
    }

    public String getEndonym()
    {
        return endonym;
    }

    public void setEndonym(String endonym)
    {
        this.endonym = endonym;
    }

    public String getPreciseEndonym()
    {
        return preciseEndonym;
    }

    public void setPreciseEndonym(String preciseEndonym)
    {
        this.preciseEndonym = preciseEndonym;
    }

    public String getDisambiguousEndonym()
    {
        return disambiguousEndonym;
    }

    public void setDisambiguousEndonym(String disambiguousEndonym)
    {
        this.disambiguousEndonym = disambiguousEndonym;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getFallbackCode()
    {
        return fallbackCode;
    }

    public void setFallbackCode(String fallbackCode)
    {
        this.fallbackCode = fallbackCode;
    }

    public List<Translation> getTranslations()
    {
        return translations;
    }

    public void setTranslations(List<Translation> translations)
    {
        this.translations = translations;
    }

    public List<DefaultText> getOriginalTexts()
    {
        return originalTexts;
    }

    public void setOriginalTexts(List<DefaultText> originalTexts)
    {
        this.originalTexts = originalTexts;
    }
}
