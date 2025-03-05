package models;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.ebean.DB;
import io.ebean.annotation.DbForeignKey;

@Entity
@Table(name = "defaultText")
public class DefaultText {
    /*
     * The ID of the text
     */
    @Id
    private int id;

    /*
     * The text
     */
    @Column(length = 4096)
    private String originalText;

    /*
     * The language of the text (mapped by its ID)
     */
    @JsonIgnore
    @DbForeignKey(noConstraint = true)
    @ManyToOne
    private Language originalLanguage;

    /*
     * The Translations available for the text
     */
    @JsonIgnore
    @OneToMany(mappedBy = "originalText")
    private List<Translation> translations;

    public DefaultText() {
    }

    public int getId() {
        return id;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public Language getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(Language originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<Translation> translations) {
        this.translations = translations;
    } 
    
    public void addTranslation(Translation translation) {
        this.translations.add(translation);
    }

    public void removeTranslation(Translation translation) {
        this.translations.remove(translation);
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
