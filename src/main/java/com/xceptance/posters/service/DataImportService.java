package com.xceptance.posters.service;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.entity.CatalogCustomer;
import com.xceptance.posters.entity.CatalogCustomerRepository;
import com.xceptance.posters.entity.CatalogDataLoader;
import com.xceptance.posters.entity.CatalogImportParser;
import com.xceptance.posters.entity.CatalogProductRepository;

/**
 * Imports initial data from XML files on application startup.
 * Uses the new entity model exclusively (catalog-import.xml + customer.xml).
 * All legacy model/repository dependencies have been removed.
 */
@Service
public class DataImportService implements CommandLineRunner
{
    private static final Logger log = LoggerFactory.getLogger(DataImportService.class);

    private final CatalogProductRepository catalogProductRepository;
    private final CatalogCustomerRepository customerRepository;
    private final PostersProperties props;
    private final LuceneSearchService luceneSearchService;
    private final CatalogDataLoader catalogDataLoader;

    public DataImportService(CatalogProductRepository catalogProductRepository,
                             CatalogCustomerRepository customerRepository,
                             PostersProperties props,
                             LuceneSearchService luceneSearchService,
                             CatalogDataLoader catalogDataLoader)
    {
        this.catalogProductRepository = catalogProductRepository;
        this.customerRepository = customerRepository;
        this.props = props;
        this.luceneSearchService = luceneSearchService;
        this.catalogDataLoader = catalogDataLoader;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception
    {
        if (catalogProductRepository.count() > 0)
        {
            log.info("Data already loaded, skipping import.");
            luceneSearchService.openReaders();
            return;
        }
        log.info("Starting data import...");

        // Import catalog data from the new XML format
        importCatalog();

        // Import demo customers
        if (props.isImportCustomer())
        {
            importCustomers();
        }

        // Build Lucene search index from catalog products
        luceneSearchService.buildIndex(java.util.Set.of("en-US", "de-DE", "sv-SE"));

        log.info("Data import complete.");
    }

    private void importCatalog()
    {
        try
        {
            InputStream xmlStream = new ClassPathResource("data/catalog-import.xml").getInputStream();
            CatalogImportParser parser = new CatalogImportParser();
            CatalogImportParser.CatalogImport catalogData = parser.parse(xmlStream);
            catalogDataLoader.load(catalogData);
            log.info("Catalog import complete.");
        }
        catch (Exception e)
        {
            log.error("Failed to import catalog data: {}", e.getMessage(), e);
        }
    }

    private void importCustomers() throws Exception
    {
        Document doc = parseXml("data/customer.xml");
        NodeList customers = doc.getElementsByTagName("customer");
        for (int i = 0; i < customers.getLength(); i++)
        {
            Element custEl = (Element) customers.item(i);
            String email = getTextContent(custEl, "email");

            if (customerRepository.existsByEmail(email))
            {
                continue;
            }

            CatalogCustomer customer = new CatalogCustomer();
            customer.setEmail(email);
            customer.hashPassword(getTextContent(custEl, "password"));
            customer.setLastName(getTextContent(custEl, "name"));
            customer.setFirstName(getTextContent(custEl, "firstName"));

            customerRepository.save(customer);
            log.info("Imported demo customer: {}", email);
        }
    }

    private Document parseXml(String path) throws Exception
    {
        InputStream is = new ClassPathResource(path).getInputStream();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(is);
    }

    private String getTextContent(Element parent, String tagName)
    {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0)
        {
            return nodes.item(0).getTextContent().trim();
        }
        return null;
    }
}
