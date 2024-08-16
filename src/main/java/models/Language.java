package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.ebean.Ebean;

@Entity
@Table(name = "langauge")
public class Language {
    /*
     * The ID of the langauge
     */
    @Id
    private int id;

    /*
     * The name of the language in the system language (english)
     */
    private String name;

    /*
     * The precise name the language is natively called, e.g. "US-American English"
     */
    private String preciseName;

    /*
     * The name speakers of this language commonly use for it, e.g. "English"
     */
    private String endonym;

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

    public Language() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    /**
     * Updates the entity in the database.
     */
    public void update()
    {
        Ebean.update(this);
    }

    /**
     * Saves the entity in the database.
     */
    public void save()
    {
        Ebean.save(this);
    }

    /**
     * Deletes the entity from the database.
     */
    public void delete()
    {
        Ebean.delete(this);
    }
}
