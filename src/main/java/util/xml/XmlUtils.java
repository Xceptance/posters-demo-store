package util.xml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public abstract class XmlUtils
{

    /**
     * Reads an xml file from the given source with the given SAX handler.
     * 
     * @param handler
     * @param source
     */
    public static void readXmlFile(DefaultHandler handler, String source)
    {
        try
        {
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            FileReader reader = new FileReader(source);
            InputSource inputSource = new InputSource(reader);
            xmlReader.setContentHandler(handler);
            xmlReader.parse(inputSource);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            e.printStackTrace();
        }
    }
}
