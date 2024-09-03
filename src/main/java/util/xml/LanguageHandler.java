package util.xml;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import models.Language;

public class LanguageHandler extends DefaultHandler {

    private String GeneralName;

    private String GeneralEndonym;

    private String GeneralCode;

    private Language Dialect;

    boolean hasDialect;

    boolean hasEndonym = false;

    boolean hasFallback = false;

    private String currentValue;

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) {
        if (localName.equals("language")) {
            hasDialect = false;
        }
        if (localName.equals("dialects")) {
            
        }
        if (localName.equals("dialect")) {
            hasDialect = true;
            Dialect = new Language();
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) {
        if (localName.equals("name")) {
            GeneralName = currentValue;
        }
        if (localName.equals("endonym")) {
            GeneralEndonym = currentValue;
        }
        if (localName.equals("code")) {
            GeneralCode = currentValue;
        }
        if (localName.equals("precisename")) {
            Dialect.setPreciseName(currentValue);
        }
        if (localName.equals("preciseendonym")) {
            Dialect.setPreciseEndonym(currentValue);
        }
        if (localName.equals("dialectendonym")) {
            Dialect.setEndonym(currentValue);
            hasEndonym = true;
        }
        if (localName.equals("disamb")) {
            Dialect.setDisambigousEndonym(currentValue);
        }
        if (localName.equals("extension")) {
            Dialect.setCode(GeneralCode + '-' + currentValue);
        }
        if (localName.equals("fallback")) {
            Dialect.setFallbackCode(currentValue);
            hasFallback = true;
        }
        if (localName.equals("dialect")) {
            if (hasEndonym == false) {
                Dialect.setEndonym(GeneralEndonym);
            }
            if (hasFallback == false) {
                Dialect.setFallbackCode(GeneralCode);
            }
            Dialect.setGroup(GeneralName);
            hasEndonym = false;
            hasFallback = false;
            Dialect.save();
        }
        if (localName.equals("language")) {
            // remove the condition to generate a base language for every family if needed 
            if (hasDialect == false) {
                Dialect = new Language();
                Dialect.setGroup(GeneralName);
                Dialect.setPreciseName(GeneralName);
                Dialect.setEndonym(GeneralEndonym);
                Dialect.setPreciseEndonym(GeneralEndonym);
                Dialect.setDisambigousEndonym(GeneralEndonym);
                Dialect.setCode(GeneralCode);
                Dialect.setFallbackCode(GeneralCode);
                Dialect.save();
            }
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) {
        currentValue = new String(ch, start, length);
    }    
}
