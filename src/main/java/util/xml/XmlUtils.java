package util.xml;

import java.io.InputStreamReader;
import java.io.Reader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Provides methods to read data from a XML file.
 * 
 * @author sebastianloob
 */
public class XmlUtils
{
    private static final Logger logger = LoggerFactory.getLogger(XmlUtils.class);

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
            final Reader reader = new InputStreamReader(XmlUtils.class.getClassLoader().getResourceAsStream(source), "UTF-8");
            final InputSource inputSource = new InputSource(reader);

            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);

            SAXParser saxParser = spf.newSAXParser();

            XMLReader xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(inputSource);
        }
        catch (final Exception e)
        {
            logger.error("Failed to read or parse XML file '{}'", source, e);
        }
    }
}
