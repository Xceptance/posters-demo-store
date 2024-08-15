package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
}
