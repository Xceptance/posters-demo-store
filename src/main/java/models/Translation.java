package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
}
