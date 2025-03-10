package models;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.ebean.DB;

@Entity
@Table(name = "supportedlanguage")
public class Language {
    /*
     * The ID of the langauge
     */
    @Id
    private int id;

    /*
     * The name of the language group in the system language (english), e.g. "english"
     */
    private String languageGroup;

    /*
     * The precise name the language is called in the system language (english), e.g. "english us"
     */
    private String preciseName;

    /*
     * The name speakers of this language commonly use for it, e.g. "English"
     */
    private String endonym;

    /*
     * The precise name the language is natively called, e.g. "US-American English"
     */
    private String preciseEndonym;

    /*
     * The endonym but annoted for disambiguity if needed, e.g. "English (US)"
     */
    private String disambigousEndonym;

    /*
     * The code the system uses to attribute text to this language, e.g. "en-US"
     */
    private String code;

    /*
     * The fallback code for this language if the specific code is not available.
     * This is meant as a means to allow a langauge to attempt to fall back to something
     * else than the default language (en-US), e.g. "de-DE" as fallback for "de-AT"
     */
    private String fallbackCode;

    /*
     * A list of all translations into that language
     */
    @JsonIgnore
    @OneToMany(mappedBy = "translationLanguage")
    private List<Translation> Translations;

    /*
     * A list of all text original to that language
     */
    @JsonIgnore
    @OneToMany(mappedBy = "originalLanguage")
    private List<DefaultText> OriginalTexts;

    public Language() {
    }

    public int getId() {
        return id;
    }

    public String getGroup() {
        return languageGroup;
    }

    public void setGroup(String group) {
        this.languageGroup = group;
    }

    public String getPreciseName() {
        return preciseName;
    }

    public void setPreciseName(String preciseName) {
        this.preciseName = preciseName;
    }

    public String getEndonym() {
        return endonym;
    }

    public void setEndonym(String endonym) {
        this.endonym = endonym;
    }

    public String getPreciseEndonym() {
        return preciseEndonym;
    }

    public void setPreciseEndonym(String preciseEndonym) {
        this.preciseEndonym = preciseEndonym;
    }

    public String getDisambigousEndonym() {
        return disambigousEndonym;
    }

    public void setDisambigousEndonym(String disambigousEndonym) {
        this.disambigousEndonym = disambigousEndonym;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFallbackCode() {
        return fallbackCode;
    }

    public void setFallbackCode(String fallbackCode) {
        this.fallbackCode = fallbackCode;
    }

    public List<Translation> getTranslations() {
        return Translations;
    }

    public void addTranslation(Translation translation) {
        this.Translations.add(translation);
    }

    public void removeTranslation(Translation translation) {
        this.Translations.remove(translation);
    }

    public List<DefaultText> getOriginalTexts() {
        return OriginalTexts;
    }

    public void addOriginalText(DefaultText text) {
        this.OriginalTexts.add(text);
    }

    public void removeOriginalText(DefaultText text) {
        this.OriginalTexts.remove(text);
    }

    /**
     * Updates the entity in the database.
     */
    public void update()
    {
        DB.update(this);
    }

    /**
     * Saves the entity in the database.
     */
    public void save()
    {
        DB.save(this);
    }

    /**
     * Deletes the entity from the database.
     */
    public void delete()
    {
        DB.delete(this);
    }
}
