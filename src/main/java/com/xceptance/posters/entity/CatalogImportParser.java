package com.xceptance.posters.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses the new catalog-import XML format into structured DTOs
 * that the CatalogDataLoader can persist.
 */
public class CatalogImportParser {

    private static final Logger log = LoggerFactory.getLogger(CatalogImportParser.class);

    // ── DTOs ────────────────────────────────────────────────────

    public record LocaleEntry(String code) {}

    public record ShippingMethodEntry(String sku, Map<String, String> names) {}

    public record SiteEntry(String name, String currency, boolean pricesAreNet, String mainLocale,
                            List<String> localeCodes, List<SiteShippingRef> shippingRefs) {}

    public record SiteShippingRef(String sku, boolean active) {}

    public record CategoryEntry(Map<String, String> names, String imagePath, List<SubCategoryEntry> subCategories) {}

    public record SubCategoryEntry(Map<String, String> names, String imagePath) {}

    public record ProductEntry(String subCategory, boolean showInCarousel, Map<String, String> names,
                               Map<String, String> shortDescriptions, Map<String, String> longDescriptions,
                               ImageSet images, Map<String, String> variationAttributes,
                               List<VariantEntry> variants) {}

    public record ImageSet(String small, String medium, String large, String original) {}

    public record VariantEntry(String sku, Map<String, String> attributes,
                               Map<String, String> prices, int inventoryQuantity) {}

    public record TaxTableEntry(String name, Map<String, String> rates) {}

    public record CatalogImport(
        List<LocaleEntry> locales,
        List<ShippingMethodEntry> shippingMethods,
        List<SiteEntry> sites,
        List<CategoryEntry> categories,
        List<ProductEntry> products,
        List<TaxTableEntry> taxTables
    ) {}

    // ── Parser ──────────────────────────────────────────────────

    public CatalogImport parse(InputStream inputStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            doc.getDocumentElement().normalize();

            log.info("Parsing catalog import XML...");

            List<LocaleEntry> locales = parseLocales(doc);
            log.info("  Parsed {} locales", locales.size());

            List<ShippingMethodEntry> shippingMethods = parseShippingMethods(doc);
            log.info("  Parsed {} shipping methods", shippingMethods.size());

            List<SiteEntry> sites = parseSites(doc);
            log.info("  Parsed {} sites", sites.size());

            List<CategoryEntry> categories = parseCategories(doc);
            int subCount = categories.stream().mapToInt(c -> c.subCategories().size()).sum();
            log.info("  Parsed {} top categories with {} subcategories", categories.size(), subCount);

            List<ProductEntry> products = parseProducts(doc);
            int variantCount = products.stream().mapToInt(p -> p.variants().size()).sum();
            log.info("  Parsed {} products with {} variants", products.size(), variantCount);

            List<TaxTableEntry> taxTables = parseTaxTables(doc);
            log.info("  Parsed {} tax tables", taxTables.size());

            log.info("Catalog import XML parsing complete.");

            return new CatalogImport(locales, shippingMethods, sites, categories, products, taxTables);

        } catch (Exception e) {
            String msg = "ERROR: Failed to parse catalog import XML: " + e.getMessage();
            log.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    // ── Section parsers ─────────────────────────────────────────

    private List<LocaleEntry> parseLocales(Document doc) {
        List<LocaleEntry> result = new ArrayList<>();
        NodeList nodes = doc.getElementsByTagName("locale");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            // Only direct children of <locales>
            if (el.getParentNode().getNodeName().equals("locales")) {
                String code = el.getAttribute("code");
                if (code.isBlank()) {
                    log.error("ERROR: <locale> element missing 'code' attribute at position {}", i);
                    throw new RuntimeException("Locale missing 'code' attribute at position " + i);
                }
                result.add(new LocaleEntry(code));
            }
        }
        return result;
    }

    private List<ShippingMethodEntry> parseShippingMethods(Document doc) {
        List<ShippingMethodEntry> result = new ArrayList<>();
        NodeList nodes = doc.getElementsByTagName("shipping-method");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            if (!el.getParentNode().getNodeName().equals("shipping-methods")) continue;
            String sku = el.getAttribute("sku");
            if (sku.isBlank()) {
                log.error("ERROR: <shipping-method> missing 'sku' attribute at position {}", i);
                throw new RuntimeException("ShippingMethod missing 'sku' at position " + i);
            }
            Map<String, String> names = parseLocalizedElements(el, "name");
            result.add(new ShippingMethodEntry(sku, names));
        }
        return result;
    }

    private List<SiteEntry> parseSites(Document doc) {
        List<SiteEntry> result = new ArrayList<>();
        NodeList nodes = doc.getElementsByTagName("site");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            if (!el.getParentNode().getNodeName().equals("sites")) continue;
            String name = el.getAttribute("name");
            String currency = el.getAttribute("currency");
            boolean pricesAreNet = Boolean.parseBoolean(el.getAttribute("prices-are-net"));
            String mainLocale = el.getAttribute("main-locale");

            if (name.isBlank() || currency.isBlank() || mainLocale.isBlank()) {
                log.error("ERROR: <site> at position {} missing required attributes (name, currency, main-locale)", i);
                throw new RuntimeException("Site missing required attributes at position " + i);
            }

            List<String> localeCodes = new ArrayList<>();
            NodeList localeRefs = el.getElementsByTagName("locale-ref");
            for (int j = 0; j < localeRefs.getLength(); j++) {
                localeCodes.add(((Element) localeRefs.item(j)).getAttribute("code"));
            }

            List<SiteShippingRef> shippingRefs = new ArrayList<>();
            NodeList shipRefs = el.getElementsByTagName("shipping-method-ref");
            for (int j = 0; j < shipRefs.getLength(); j++) {
                Element ref = (Element) shipRefs.item(j);
                shippingRefs.add(new SiteShippingRef(
                    ref.getAttribute("sku"),
                    Boolean.parseBoolean(ref.getAttribute("active"))
                ));
            }

            result.add(new SiteEntry(name, currency, pricesAreNet, mainLocale, localeCodes, shippingRefs));
        }
        return result;
    }

    private List<CategoryEntry> parseCategories(Document doc) {
        List<CategoryEntry> result = new ArrayList<>();
        NodeList nodes = doc.getElementsByTagName("category");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            if (!el.getParentNode().getNodeName().equals("categories")) continue;
            Map<String, String> names = parseLocalizedElements(el, "name");

            String imagePath = el.getAttribute("image").isEmpty() ? null : el.getAttribute("image");

            List<SubCategoryEntry> subs = new ArrayList<>();
            NodeList subNodes = el.getElementsByTagName("sub-category");
            for (int j = 0; j < subNodes.getLength(); j++) {
                Element subEl = (Element) subNodes.item(j);
                String subImagePath = subEl.getAttribute("image").isEmpty() ? null : subEl.getAttribute("image");
                subs.add(new SubCategoryEntry(parseLocalizedElements(subEl, "name"), subImagePath));
            }
            result.add(new CategoryEntry(names, imagePath, subs));
        }
        return result;
    }

    private List<ProductEntry> parseProducts(Document doc) {
        List<ProductEntry> result = new ArrayList<>();
        NodeList nodes = doc.getElementsByTagName("product");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            if (!el.getParentNode().getNodeName().equals("products")) continue;

            String subCategory = el.getAttribute("sub-category");
            boolean showInCarousel = Boolean.parseBoolean(el.getAttribute("show-in-carousel"));
            if (subCategory.isBlank()) {
                log.error("ERROR: <product> at position {} missing 'sub-category' attribute", i);
                throw new RuntimeException("Product missing 'sub-category' at position " + i);
            }

            Map<String, String> names = parseLocalizedElements(el, "name");
            Map<String, String> shortDescs = parseLocalizedElements(el, "short-description");
            Map<String, String> longDescs = parseLocalizedElements(el, "long-description");

            ImageSet images = null;
            NodeList imgNodes = el.getElementsByTagName("images");
            if (imgNodes.getLength() > 0) {
                Element imgEl = (Element) imgNodes.item(0);
                images = new ImageSet(
                    getDirectChildText(imgEl, "small"),
                    getDirectChildText(imgEl, "medium"),
                    getDirectChildText(imgEl, "large"),
                    getDirectChildText(imgEl, "original")
                );
            }

            Map<String, String> varAttrs = new LinkedHashMap<>();
            NodeList attrNodes = el.getElementsByTagName("attribute");
            for (int j = 0; j < attrNodes.getLength(); j++) {
                Element attrEl = (Element) attrNodes.item(j);
                varAttrs.put(attrEl.getAttribute("name"), attrEl.getAttribute("values"));
            }

            List<VariantEntry> variants = new ArrayList<>();
            NodeList variantNodes = el.getElementsByTagName("variant");
            for (int j = 0; j < variantNodes.getLength(); j++) {
                Element varEl = (Element) variantNodes.item(j);
                String sku = varEl.getAttribute("sku");
                if (sku.isBlank()) {
                    log.error("ERROR: <variant> in product '{}' missing 'sku'", names.values().stream().findFirst().orElse("?"));
                    throw new RuntimeException("Variant missing 'sku' in product at position " + i);
                }

                // Collect all non-standard attributes as variant attributes
                Map<String, String> attrs = new LinkedHashMap<>();
                var namedMap = varEl.getAttributes();
                for (int k = 0; k < namedMap.getLength(); k++) {
                    var attr = namedMap.item(k);
                    if (!attr.getNodeName().equals("sku")) {
                        attrs.put(attr.getNodeName(), attr.getNodeValue());
                    }
                }

                Map<String, String> prices = new LinkedHashMap<>();
                NodeList priceNodes = varEl.getElementsByTagName("price");
                for (int k = 0; k < priceNodes.getLength(); k++) {
                    Element priceEl = (Element) priceNodes.item(k);
                    prices.put(priceEl.getAttribute("currency"), priceEl.getTextContent().trim());
                }

                int invQty = 0;
                NodeList invNodes = varEl.getElementsByTagName("inventory");
                if (invNodes.getLength() > 0) {
                    invQty = Integer.parseInt(((Element) invNodes.item(0)).getAttribute("quantity"));
                }

                variants.add(new VariantEntry(sku, attrs, prices, invQty));
            }

            result.add(new ProductEntry(subCategory, showInCarousel, names, shortDescs, longDescs, images, varAttrs, variants));
        }
        return result;
    }

    private List<TaxTableEntry> parseTaxTables(Document doc) {
        List<TaxTableEntry> result = new ArrayList<>();
        NodeList nodes = doc.getElementsByTagName("tax-table");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            String name = el.getAttribute("name");
            Map<String, String> rates = new LinkedHashMap<>();
            NodeList rateNodes = el.getElementsByTagName("rate");
            for (int j = 0; j < rateNodes.getLength(); j++) {
                Element rate = (Element) rateNodes.item(j);
                rates.put(rate.getAttribute("sku"), rate.getAttribute("rate"));
            }
            result.add(new TaxTableEntry(name, rates));
        }
        return result;
    }

    // ── Helpers ──────────────────────────────────────────────────

    private Map<String, String> parseLocalizedElements(Element parent, String tagName) {
        Map<String, String> result = new LinkedHashMap<>();
        NodeList nodes = parent.getElementsByTagName(tagName);
        for (int i = 0; i < nodes.getLength(); i++) {
            Element el = (Element) nodes.item(i);
            // Only direct children
            if (el.getParentNode().equals(parent)) {
                String lang = el.getAttribute("xml:lang");
                result.put(lang, el.getTextContent().trim());
            }
        }
        return result;
    }

    private String getDirectChildText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent().trim();
        }
        return null;
    }
}
