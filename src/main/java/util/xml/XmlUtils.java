package util.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Provides methods to read data from a XML file.
 * 
 * @author sebastianloob
 */
public class XmlUtils
{
    /**
     * Reads an xml file from the given source with the given SAX handler.
     * 
     * @param handler
     * @param source
     */
    public static void readXmlFile(final DefaultHandler handler, final String source)
    {
        try
        {
            final XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            final Reader reader = new InputStreamReader(XmlUtils.class.getClassLoader().getResourceAsStream(source), "UTF-8");
            final InputSource inputSource = new InputSource(reader);
            xmlReader.setContentHandler(handler);
            xmlReader.parse(inputSource);
        }
        catch (final FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        catch (final SAXException e)
        {
            e.printStackTrace();
        }
    }
}
