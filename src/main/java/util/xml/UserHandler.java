package util.xml;

import models_backoffice.User;


import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This {@link DefaultHandler} parses an XML file with user data and persists them in the database.
 * 
 * @author kennygozali
 */
public class UserHandler extends DefaultHandler
{

    private User user;


    private String currentValue;

    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts)
    {
        if (localName.equals("user"))
        {
            user = new User();
            user.save();
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName)
    {
        if (localName.equals("user"))
        {
            user.update();
        }
        if (localName.equals("email"))
        {
            user.setEmail(currentValue);
        }
        if (localName.equals("password"))
        {
            user.hashPasswd(currentValue);
        }
        if (localName.equals("name"))
        {
            user.setName(currentValue);
        }
        if (localName.equals("firstName"))
        {
            user.setFirstName(currentValue);
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length)
    {
        currentValue = new String(ch, start, length);
    }
}
