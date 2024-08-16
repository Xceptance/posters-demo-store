package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.ebean.Ebean;
import io.ebean.annotation.Index;

@Index(unique = true, columnNames = {"textId", "translationLangauge"})
@Entity
@Table(name = "translation")
public class Translation {
    /*
     * The ID of the translation
     */
    @Id
    private int id;

    /*
     * The text id of the original text the translation is for
     */
    private int textId;

    /*
     * The language of the translation
     */
    private String translationLanguage;

    /*
     * The translated text
     */
    @Column(length = 4096)
    private String translationText;

    public Translation() {
    }

    public int getId() {
        return id;
    }

    public int getTextId() {
        return textId;
    }

    public void setTextId(int textId) {
        this.textId = textId;
    }

    public String getTranslationLanguage() {
        return translationLanguage;
    }

    public void setTranslationLanguage(String translationLanguage) {
        this.translationLanguage = translationLanguage;
    }

    public String getTranslationText() {
        return translationText;
    }

    public void setTranslationText(String translationText) {
        this.translationText = translationText;
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
