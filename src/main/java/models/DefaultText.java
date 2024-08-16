package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.ebean.Ebean;

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
     * The language of the text
     */
    private String originalLanguage;

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

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
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
