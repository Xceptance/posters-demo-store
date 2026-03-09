package com.xceptance.posters.entity;

import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Loads parsed catalog import data into the database.
 * Processes sections in dependency order: locales → shipping → sites → categories → products → taxes.
 */
@Service
public class CatalogDataLoader {

    private static final Logger log = LoggerFactory.getLogger(CatalogDataLoader.class);

    private final EntityManager em;
    private final AtomicInteger textIdSeq = new AtomicInteger(1000);

    public CatalogDataLoader(EntityManager em) {
        this.em = em;
    }

    @Transactional
    public void load(CatalogImportParser.CatalogImport data) {
        log.info("=== Starting catalog data import ===");

        Map<String, Locale> localeMap = loadLocales(data);
        Map<String, ShippingMethod> shippingMap = loadShippingMethods(data, localeMap);
        Map<String, PriceTable> priceTableMap = loadSites(data, localeMap, shippingMap);
        Map<String, Category> categoryMap = loadCategories(data, localeMap);
        loadProducts(data, categoryMap, localeMap, priceTableMap);
        loadTaxTables(data);

        em.flush();
        log.info("=== Catalog data import complete ===");
    }

    private Map<String, Locale> loadLocales(CatalogImportParser.CatalogImport data) {
        Map<String, Locale> map = new HashMap<>();
        for (var entry : data.locales()) {
            Locale locale = new Locale();
            locale.setLocale(entry.code());
            em.persist(locale);
            map.put(entry.code(), locale);
            log.info("  [Locale] Created: {}", entry.code());
        }
        log.info("  Loaded {} locales", map.size());
        return map;
    }

    private Map<String, ShippingMethod> loadShippingMethods(CatalogImportParser.CatalogImport data,
                                                             Map<String, Locale> localeMap) {
        Map<String, ShippingMethod> map = new HashMap<>();
        for (var entry : data.shippingMethods()) {
            Integer nameTextId = persistLocalizedTexts(entry.names(), localeMap);

            ShippingMethod sm = new ShippingMethod();
            sm.setSku(entry.sku());
            sm.setNameTextId(nameTextId);
            em.persist(sm);
            map.put(entry.sku(), sm);
            log.info("  [ShippingMethod] Created: {} ({})", entry.sku(),
                entry.names().values().stream().findFirst().orElse("unnamed"));
        }
        log.info("  Loaded {} shipping methods", map.size());
        return map;
    }

    /**
     * Creates Sites, PriceTables, TaxTables, InventoryTables.
     * Returns a map of currency → PriceTable so products can write prices.
     */
    private Map<String, PriceTable> loadSites(CatalogImportParser.CatalogImport data,
                           Map<String, Locale> localeMap,
                           Map<String, ShippingMethod> shippingMap) {
        Map<String, PriceTable> priceTableMap = new HashMap<>();

        for (var entry : data.sites()) {
            Locale mainLocale = localeMap.get(entry.mainLocale());
            if (mainLocale == null) {
                String msg = String.format("ERROR: Site '%s' references unknown main-locale '%s'",
                    entry.name(), entry.mainLocale());
                log.error(msg);
                throw new RuntimeException(msg);
            }

            PriceTable pt = new PriceTable();
            pt.setName(entry.name() + " Prices");
            pt.setCurrency(entry.currency());
            em.persist(pt);
            priceTableMap.put(entry.currency(), pt);

            TaxTable tt = new TaxTable();
            tt.setName(entry.name() + " Tax");
            em.persist(tt);

            Site site = new Site();
            site.setName(entry.name());
            site.setCurrency(entry.currency());
            site.setPricesAreNet(entry.pricesAreNet());
            site.setMainLocale(mainLocale);
            site.setPriceTable(pt);
            site.setTaxTable(tt);
            em.persist(site);

            // Create inventory table for this site
            InventoryTable invTable = new InventoryTable();
            invTable.setSite(site);
            invTable.setName(entry.name() + " Inventory");
            em.persist(invTable);

            em.flush();

            for (var ref : entry.shippingRefs()) {
                ShippingMethod sm = shippingMap.get(ref.sku());
                if (sm == null) {
                    log.warn("  WARNING: Site '{}' references unknown shipping method '{}'",
                        entry.name(), ref.sku());
                    continue;
                }
                SiteShippingMethod ssm = new SiteShippingMethod();
                ssm.setSite(site);
                ssm.setShippingMethod(sm);
                ssm.setActive(ref.active());
                em.persist(ssm);
            }

            log.info("  [Site] Created: {} (currency={}, locales={}, shipping={})",
                entry.name(), entry.currency(), entry.localeCodes().size(), entry.shippingRefs().size());
        }
        log.info("  Loaded {} sites", data.sites().size());
        return priceTableMap;
    }

    private Map<String, Category> loadCategories(CatalogImportParser.CatalogImport data,
                                                  Map<String, Locale> localeMap) {
        Map<String, Category> subCategoryMap = new HashMap<>();
        for (var catEntry : data.categories()) {
            Integer catNameId = persistLocalizedTexts(catEntry.names(), localeMap);

            Category topCat = new Category();
            topCat.setNameTextId(catNameId);
            topCat.setImagePath(catEntry.imagePath());
            em.persist(topCat);

            String topCatDisplayName = catEntry.names().values().stream().findFirst().orElse("?");
            log.info("  [Category] Created top: {}", topCatDisplayName);

            for (var subEntry : catEntry.subCategories()) {
                Integer subNameId = persistLocalizedTexts(subEntry.names(), localeMap);

                Category subCat = new Category();
                subCat.setNameTextId(subNameId);
                subCat.setParent(topCat);
                subCat.setImagePath(subEntry.imagePath());
                em.persist(subCat);

                String subDisplayName = subEntry.names().values().stream().findFirst().orElse("?");
                subCategoryMap.put(subDisplayName, subCat);
                log.info("  [Category]   Created sub: {} → {}", topCatDisplayName, subDisplayName);
            }
        }
        log.info("  Loaded {} subcategories", subCategoryMap.size());
        return subCategoryMap;
    }

    private void loadProducts(CatalogImportParser.CatalogImport data,
                              Map<String, Category> categoryMap,
                              Map<String, Locale> localeMap,
                              Map<String, PriceTable> priceTableMap) {
        int productCount = 0;
        int variantCount = 0;

        // Cache for reusable VariationAttribute entities (keyed by name, e.g. "size", "finish")
        Map<String, VariationAttribute> attrCache = new HashMap<>();

        // Cache for VariationAttributeValue entities (keyed by "attrName:value", e.g. "size:16x12")
        Map<String, VariationAttributeValue> attrValueCache = new HashMap<>();

        // Get inventory tables (one per site) for storing inventory entries
        @SuppressWarnings("unchecked")
        var inventoryTables = em.createQuery("SELECT it FROM InventoryTable it").getResultList();

        for (var prodEntry : data.products()) {
            Category subCat = categoryMap.get(prodEntry.subCategory());
            if (subCat == null) {
                String msg = String.format("ERROR: Product '%s' references unknown sub-category '%s'",
                    prodEntry.names().values().stream().findFirst().orElse("?"), prodEntry.subCategory());
                log.error(msg);
                throw new RuntimeException(msg);
            }

            Integer nameId = persistLocalizedTexts(prodEntry.names(), localeMap);
            Integer shortDescId = persistLocalizedTexts(prodEntry.shortDescriptions(), localeMap);
            Integer longDescId = persistLocalizedTexts(prodEntry.longDescriptions(), localeMap);

            // Derive a product SKU from the first variant's SKU prefix
            String productSku;
            if (!prodEntry.variants().isEmpty()) {
                String firstVarSku = prodEntry.variants().get(0).sku();
                int dashIdx = firstVarSku.lastIndexOf('-');
                productSku = dashIdx > 0 ? firstVarSku.substring(0, dashIdx) : firstVarSku;
            } else {
                productSku = "PROD" + String.format("%06d", productCount + 1);
            }

            Product product = new Product();
            product.setSku(productSku);
            product.setNameTextId(nameId);
            product.setDescriptionOverviewTextId(shortDescId);
            product.setDescriptionDetailTextId(longDescId);
            product.setShowInCarousel(prodEntry.showInCarousel());

            if (prodEntry.images() != null) {
                product.setSmallImageUrl(prodEntry.images().small());
                product.setMediumImageUrl(prodEntry.images().medium());
                product.setLargeImageUrl(prodEntry.images().large());
                product.setOriginalImageUrl(prodEntry.images().original());
            }

            product.getCategories().add(subCat);
            em.persist(product);
            productCount++;

            String prodDisplayName = prodEntry.names().values().stream().findFirst().orElse("?");

            // Create variants with attributes, prices, and inventory
            int varNum = 1;
            for (var varEntry : prodEntry.variants()) {
                Variant variant = new Variant();
                variant.setProduct(product);
                variant.setVariantNumber(varNum);
                em.persist(variant);
                em.flush(); // ensure variant has an ID

                // Compute the full SKU that controllers will use for prices:
                // {productSku}-{variantNumber padded to 4 digits}
                String fullSku = String.format("%s-%04d", productSku, varNum);

                // Process variant attributes (e.g., size="16x12", finish="matte")
                for (var attrEntry : varEntry.attributes().entrySet()) {
                    String attrName = attrEntry.getKey();   // e.g., "size"
                    String attrValue = attrEntry.getValue(); // e.g., "16x12"

                    // Get or create the VariationAttribute
                    VariationAttribute va = attrCache.computeIfAbsent(attrName, name -> {
                        VariationAttribute newVa = new VariationAttribute();
                        newVa.setName(name);
                        em.persist(newVa);
                        return newVa;
                    });

                    // Also link attribute to the product
                    product.getVariationAttributes().add(va);

                    // Get or create the VariationAttributeValue
                    String cacheKey = attrName + ":" + attrValue;
                    VariationAttributeValue vav = attrValueCache.computeIfAbsent(cacheKey, key -> {
                        VariationAttributeValue newVav = new VariationAttributeValue();
                        newVav.setAttribute(va);
                        newVav.setValue(attrValue);
                        em.persist(newVav);
                        return newVav;
                    });

                    // Link to variant
                    variant.getAttributeValues().add(vav);
                }

                // Create Price entries for this variant
                for (var priceEntry : varEntry.prices().entrySet()) {
                    String currency = priceEntry.getKey();
                    BigDecimal amount = new BigDecimal(priceEntry.getValue());

                    PriceTable pt = priceTableMap.get(currency);
                    if (pt != null) {
                        Price price = new Price();
                        price.setSku(fullSku);
                        price.setPriceTable(pt);
                        price.setPrice(amount);
                        em.persist(price);
                    } else {
                        log.warn("  WARNING: No price table for currency '{}' (variant {})", currency, varEntry.sku());
                    }
                }

                // Create InventoryEntry for each inventory table (site)
                for (Object itObj : inventoryTables) {
                    InventoryTable invTable = (InventoryTable) itObj;
                    InventoryEntry ie = new InventoryEntry();
                    ie.setInventoryTable(invTable);
                    ie.setSku(fullSku);
                    ie.setAvailableQuantity(varEntry.inventoryQuantity());
                    em.persist(ie);
                }

                variantCount++;
                varNum++;
                log.info("  [Variant] Created: {} → {} (attrs={}, prices={})",
                    prodDisplayName, fullSku,
                    varEntry.attributes().size(), varEntry.prices().size());
            }

            log.info("  [Product] Created: {} (sku={}, carousel={}, {} variants)",
                prodDisplayName, productSku, prodEntry.showInCarousel(), prodEntry.variants().size());
        }
        log.info("  Loaded {} products with {} variants", productCount, variantCount);
    }

    private void loadTaxTables(CatalogImportParser.CatalogImport data) {
        for (var ttEntry : data.taxTables()) {
            log.info("  [TaxTable] Processing: {} ({} rates)", ttEntry.name(), ttEntry.rates().size());
        }
        log.info("  Processed {} tax table definitions", data.taxTables().size());
    }

    // ── Helpers ──────────────────────────────────────────────────

    /**
     * Creates LocalizedText rows for all locale→text mappings and returns the shared textId.
     * If no locales match, creates rows for the first locale found.
     */
    private Integer persistLocalizedTexts(Map<String, String> localizedValues,
                                           Map<String, Locale> localeMap) {
        if (localizedValues.isEmpty()) {
            return null;
        }

        int textId = textIdSeq.getAndIncrement();
        Locale fallbackLocale = localeMap.values().iterator().next();

        for (var entry : localizedValues.entrySet()) {
            Locale locale = localeMap.get(entry.getKey());
            if (locale == null) {
                // Use fallback if locale code from XML doesn't match loaded locales
                locale = fallbackLocale;
            }

            LocalizedText lt = new LocalizedText();
            lt.setTextId(textId);
            lt.setLocale(locale);
            lt.setText(entry.getValue());
            em.persist(lt);
        }

        return textId;
    }
}
