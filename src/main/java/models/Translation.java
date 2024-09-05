package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import io.ebean.Ebean;
import io.ebean.annotation.DbForeignKey;
import io.ebean.annotation.Index;

//@Index(unique = true, columnNames = {"originalText", "translationLanguage"})
@Entity
@Table(name = "translation")
public class Translation {
    /*
     * The ID of the translation
     */
    @Id
    private int id;

    /*
     * The text (mapped by its ID) of the original text the translation is for
     */
    @DbForeignKey(noConstraint = true)
    @ManyToOne(optional=false)
    private DefaultText originalText;

    /*
     * The language of the translation (mapped by its ID)
     */
    @DbForeignKey(noConstraint = true)
    @ManyToOne
    private Language translationLanguage;

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

    public DefaultText getText() {
        return originalText;
    }

    public void setText(DefaultText text) {
        this.originalText = text;
    }

    public Language getTranslationLanguageId() {
        return translationLanguage;
    }

    public void setTranslationLanguageId(Language translationLanguage) {
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
