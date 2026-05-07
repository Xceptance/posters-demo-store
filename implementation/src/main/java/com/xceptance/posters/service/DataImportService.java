package com.xceptance.posters.service;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Set;

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
import com.xceptance.posters.entity.Customer;
import com.xceptance.posters.entity.CustomerProfile;
import com.xceptance.posters.repository.CustomerRepository;
import com.xceptance.posters.repository.CustomerProfileRepository;
import com.xceptance.posters.util.CatalogDataLoader;
import com.xceptance.posters.util.CatalogImportParser;
import com.xceptance.posters.repository.CatalogProductRepository;

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
    private final CustomerRepository customerRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final CustomerSearchService customerSearchService;
    private final PostersProperties props;
    private final LuceneSearchService luceneSearchService;
    private final CatalogDataLoader catalogDataLoader;

    public DataImportService(CatalogProductRepository catalogProductRepository,
                             CustomerRepository customerRepository,
                             CustomerProfileRepository customerProfileRepository,
                             CustomerSearchService customerSearchService,
                             PostersProperties props,
                             LuceneSearchService luceneSearchService,
                             CatalogDataLoader catalogDataLoader)
    {
        this.catalogProductRepository = catalogProductRepository;
        this.customerRepository = customerRepository;
        this.customerProfileRepository = customerProfileRepository;
        this.customerSearchService = customerSearchService;
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
        luceneSearchService.buildIndex(Set.of("en-US", "de-DE", "sv-SE", "ja-JP"));

        // Trigger async search indexer for customers after XML import
        customerSearchService.triggerFullIndex();

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

            Customer customer = new Customer();
            customer.setEmail(email);
            customer.hashPassword(getTextContent(custEl, "password"));
            customer.setLastName(getTextContent(custEl, "name"));
            customer.setFirstName(getTextContent(custEl, "firstName"));

            customer = customerRepository.save(customer);

            final CustomerProfile profile = new CustomerProfile();
            profile.setCustomer(customer);
            profile.setPassword(customer.getPassword());
            profile.setLastPasswordChange(LocalDateTime.now());
            customerProfileRepository.save(profile);

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
