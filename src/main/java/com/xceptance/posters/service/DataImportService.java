package com.xceptance.posters.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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
import com.xceptance.posters.model.*;
import com.xceptance.posters.repository.*;

/**
 * Imports initial data from XML files on application startup.
 * Replaces Ninja's DataImport utility.
 */
@Service
public class DataImportService implements CommandLineRunner
{
    private static final Logger log = LoggerFactory.getLogger(DataImportService.class);

    private final LanguageRepository languageRepository;
    private final DefaultTextRepository defaultTextRepository;
    private final TranslationRepository translationRepository;
    private final TopCategoryRepository topCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductRepository productRepository;
    private final PosterSizeRepository posterSizeRepository;
    private final ProductPosterSizeRepository productPosterSizeRepository;
    private final CustomerRepository customerRepository;
    private final PostersProperties props;

    // Lookup caches built during import
    private final Map<String, Language> languagesByCode = new HashMap<>();
    private final Map<String, SubCategory> subCategoriesByName = new HashMap<>();
    private final Map<String, PosterSize> sizeCache = new HashMap<>();

    public DataImportService(LanguageRepository languageRepository,
                             DefaultTextRepository defaultTextRepository,
                             TranslationRepository translationRepository,
                             TopCategoryRepository topCategoryRepository,
                             SubCategoryRepository subCategoryRepository,
                             ProductRepository productRepository,
                             PosterSizeRepository posterSizeRepository,
                             ProductPosterSizeRepository productPosterSizeRepository,
                             CustomerRepository customerRepository,
                             PostersProperties props)
    {
        this.languageRepository = languageRepository;
        this.defaultTextRepository = defaultTextRepository;
        this.translationRepository = translationRepository;
        this.topCategoryRepository = topCategoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.productRepository = productRepository;
        this.posterSizeRepository = posterSizeRepository;
        this.productPosterSizeRepository = productPosterSizeRepository;
        this.customerRepository = customerRepository;
        this.props = props;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception
    {
        if (topCategoryRepository.count() > 0)
        {
            log.info("Data already loaded, skipping import.");
            return;
        }
        log.info("Starting data import...");
        importLanguages();
        importCategories();
        importProducts();
        if (props.isImportCustomer())
        {
            importCustomers();
        }
        log.info("Data import complete.");
    }

    private Document parseXml(String path) throws Exception
    {
        InputStream is = new ClassPathResource(path).getInputStream();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(is);
    }

    private void importLanguages() throws Exception
    {
        Document doc = parseXml("data/languages.xml");
        NodeList languages = doc.getElementsByTagName("language");
        for (int i = 0; i < languages.getLength(); i++)
        {
            Element langEl = (Element) languages.item(i);
            String langName = getTextContent(langEl, "name");
            String endonym = getTextContent(langEl, "endonym");
            String code = getTextContent(langEl, "code");
            NodeList dialects = langEl.getElementsByTagName("dialect");
            for (int j = 0; j < dialects.getLength(); j++)
            {
                Element dialectEl = (Element) dialects.item(j);
                Language lang = new Language();
                lang.setLanguageGroup(langName);
                lang.setEndonym(endonym);
                lang.setCode(code);
                lang.setPreciseName(getTextContent(dialectEl, "precisename"));
                lang.setPreciseEndonym(getTextContent(dialectEl, "preciseendonym"));
                lang.setDisambiguousEndonym(getTextContent(dialectEl, "disamb"));
                String extension = getTextContent(dialectEl, "extension");
                String fullCode = code + "-" + extension;
                lang.setFallbackCode(fullCode);
                lang = languageRepository.save(lang);
                languagesByCode.put(fullCode, lang);
            }
        }
        log.info("Imported {} languages.", languagesByCode.size());
    }

    private void importCategories() throws Exception
    {
        Document doc = parseXml("data/categories.xml");
        NodeList categories = doc.getElementsByTagName("category");
        for (int i = 0; i < categories.getLength(); i++)
        {
            Element catEl = (Element) categories.item(i);
            DefaultText catName = createI18nText(catEl, "nameCategory");

            TopCategory topCat = new TopCategory();
            topCat.setName(catName);
            topCat = topCategoryRepository.save(topCat);

            NodeList subCats = catEl.getElementsByTagName("subCategory");
            for (int j = 0; j < subCats.getLength(); j++)
            {
                Element subEl = (Element) subCats.item(j);
                DefaultText subName = createI18nText(subEl, "nameSubCategory");

                SubCategory subCat = new SubCategory();
                subCat.setName(subName);
                subCat.setTopCategory(topCat);
                subCat = subCategoryRepository.save(subCat);
                subCategoriesByName.put(subName.getOriginalText(), subCat);
            }
        }
        log.info("Imported {} top categories, {} sub categories.",
                topCategoryRepository.count(), subCategoryRepository.count());
    }

    private void importProducts() throws Exception
    {
        Document doc = parseXml("data/products.xml");
        NodeList products = doc.getElementsByTagName("product");
        int count = 0;

        for (int i = 0; i < products.getLength(); i++)
        {
            Element prodEl = (Element) products.item(i);

            DefaultText nameText = createI18nText(prodEl, "name");
            DefaultText shortDescText = createI18nText(prodEl, "shortDescription");
            DefaultText longDescText = createI18nText(prodEl, "longDescription");

            Element imgEl = (Element) prodEl.getElementsByTagName("imageURL").item(0);
            String subCategoryName = getTextContent(prodEl, "subCategory");
            SubCategory subCat = subCategoriesByName.get(subCategoryName);

            Product product = new Product();
            product.setName(nameText);
            product.setDescriptionOverview(shortDescText);
            product.setDescriptionDetail(longDescText);
            product.setSmallImageURL(getTextContent(imgEl, "small"));
            product.setMediumImageURL(getTextContent(imgEl, "medium"));
            product.setLargeImageURL(getTextContent(imgEl, "large"));
            product.setOriginalImageURL(getTextContent(imgEl, "original"));
            product.setImageURL(getTextContent(imgEl, "medium"));
            product.setShowInCarousel(false);
            product.setSubCategory(subCat);
            if (subCat != null)
            {
                product.setTopCategory(subCat.getTopCategory());
            }

            // Parse sizes and prices
            String sizesStr = getTextContent(prodEl, "availableSize");
            String pricesStr = getTextContent(prodEl, "price");
            product = productRepository.save(product);

            if (sizesStr != null && pricesStr != null)
            {
                String[] sizes = sizesStr.split(";");
                String[] prices = pricesStr.split(";");
                double minPrice = Double.MAX_VALUE;

                for (int j = 0; j < sizes.length && j < prices.length; j++)
                {
                    String[] dims = sizes[j].trim().split("x");
                    if (dims.length != 2) continue;
                    int w = Integer.parseInt(dims[0].trim());
                    int h = Integer.parseInt(dims[1].trim());
                    double price;
                    try
                    {
                        price = Double.parseDouble(prices[j].trim());
                    }
                    catch (NumberFormatException e)
                    {
                        price = 0;
                    }

                    PosterSize size = getOrCreateSize(w, h);
                    ProductPosterSize pps = new ProductPosterSize();
                    pps.setProduct(product);
                    pps.setSize(size);
                    pps.setPrice(price);
                    productPosterSizeRepository.save(pps);
                    if (price < minPrice) minPrice = price;
                }
                if (minPrice < Double.MAX_VALUE)
                {
                    product.setMinimumPrice(minPrice);
                    productRepository.save(product);
                }
            }
            count++;
        }
        // Mark some products for carousel
        for (int id : new int[]{1, 2, 5, 11, 15, 25, 35, 50, 65, 80})
        {
            productRepository.findById(id).ifPresent(p -> {
                p.setShowInCarousel(true);
                productRepository.save(p);
            });
        }
        log.info("Imported {} products.", count);
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
            customer.setName(getTextContent(custEl, "name"));
            customer.setFirstName(getTextContent(custEl, "firstName"));

            // Shipping address
            NodeList shipNodes = custEl.getElementsByTagName("shippingAddress");
            if (shipNodes.getLength() > 0)
            {
                Element shipEl = (Element) shipNodes.item(0);
                ShippingAddress sa = new ShippingAddress();
                sa.setName(getTextContent(shipEl, "delName"));
                sa.setFirstName(getTextContent(shipEl, "delFirstName"));
                sa.setCompany(getTextContent(shipEl, "delCompany"));
                sa.setAddressLine(getTextContent(shipEl, "delAddressLine"));
                sa.setCity(getTextContent(shipEl, "delCity"));
                sa.setState(getTextContent(shipEl, "delState"));
                sa.setCountry(getTextContent(shipEl, "delCountry"));
                sa.setZip(getTextContent(shipEl, "delZip"));
                customer.addShippingAddress(sa);
            }

            // Billing address
            NodeList billNodes = custEl.getElementsByTagName("billingAddress");
            if (billNodes.getLength() > 0)
            {
                Element billEl = (Element) billNodes.item(0);
                BillingAddress ba = new BillingAddress();
                ba.setName(getTextContent(billEl, "billName"));
                ba.setFirstName(getTextContent(billEl, "billFirstName"));
                ba.setCompany(getTextContent(billEl, "billCompany"));
                ba.setAddressLine(getTextContent(billEl, "billAddressLine"));
                ba.setCity(getTextContent(billEl, "billCity"));
                ba.setState(getTextContent(billEl, "billState"));
                ba.setCountry(getTextContent(billEl, "billCountry"));
                ba.setZip(getTextContent(billEl, "billZip"));
                customer.addBillingAddress(ba);
            }

            // Credit card
            NodeList payNodes = custEl.getElementsByTagName("paymentMethod");
            if (payNodes.getLength() > 0)
            {
                Element payEl = (Element) payNodes.item(0);
                CreditCard cc = new CreditCard();
                cc.setName(getTextContent(payEl, "paymentName"));
                cc.setCardNumber(getTextContent(payEl, "cardNumber"));
                cc.setMonth(Integer.parseInt(getTextContent(payEl, "month")));
                cc.setYear(Integer.parseInt(getTextContent(payEl, "year")));
                customer.addCreditCard(cc);
            }

            customerRepository.save(customer);
            log.info("Imported demo customer: {}", email);
        }
    }

    /**
     * Creates a DefaultText with translations from the given element.
     * Elements with xml:lang="x-default" are the original text,
     * others (e.g. "de-DE") are translations.
     */
    private DefaultText createI18nText(Element parent, String tagName)
    {
        NodeList nodes = parent.getElementsByTagName(tagName);
        DefaultText dt = new DefaultText();

        // First pass: find the default text
        for (int i = 0; i < nodes.getLength(); i++)
        {
            Element el = (Element) nodes.item(i);
            // Only process direct children
            if (!el.getParentNode().equals(parent)) continue;
            String lang = el.getAttribute("xml:lang");
            if ("x-default".equals(lang) || lang.isEmpty())
            {
                dt.setOriginalText(el.getTextContent().trim());
                break;
            }
        }
        dt = defaultTextRepository.save(dt);

        // Second pass: add translations
        for (int i = 0; i < nodes.getLength(); i++)
        {
            Element el = (Element) nodes.item(i);
            if (!el.getParentNode().equals(parent)) continue;
            String lang = el.getAttribute("xml:lang");
            if (!"x-default".equals(lang) && !lang.isEmpty())
            {
                Language language = languagesByCode.get(lang);
                if (language != null)
                {
                    Translation t = new Translation();
                    t.setOriginalText(dt);
                    t.setTranslationLanguage(language);
                    t.setTranslationText(el.getTextContent().trim());
                    translationRepository.save(t);
                }
            }
        }
        return dt;
    }

    private PosterSize getOrCreateSize(int w, int h)
    {
        String key = w + "x" + h;
        if (sizeCache.containsKey(key))
        {
            return sizeCache.get(key);
        }
        PosterSize size = posterSizeRepository.findByWidthAndHeight(w, h);
        if (size == null)
        {
            size = new PosterSize();
            size.setWidth(w);
            size.setHeight(h);
            size = posterSizeRepository.save(size);
        }
        sizeCache.put(key, size);
        return size;
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
