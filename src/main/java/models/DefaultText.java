package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
}
