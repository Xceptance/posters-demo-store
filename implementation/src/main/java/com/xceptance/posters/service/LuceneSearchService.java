package com.xceptance.posters.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.repository.CatalogProductRepository;
import com.xceptance.posters.service.LocalizedTextService;
import com.xceptance.posters.entity.Product;

/**
 * Manages per-language Lucene indices for full-text product search
 * with language-specific analyzers and stemmers.
 *
 * Now uses new entity model (Product from entity package, LocalizedTextService)
 * instead of legacy model.
 */
@Service
public class LuceneSearchService implements DisposableBean
{
    private static final Logger log = LoggerFactory.getLogger(LuceneSearchService.class);

    private static final String FIELD_PRODUCT_ID = "productId";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_DESC_OVERVIEW = "descriptionOverview";
    private static final String FIELD_DESC_DETAIL = "descriptionDetail";

    private static final String[] SEARCH_FIELDS = {FIELD_NAME, FIELD_DESC_OVERVIEW, FIELD_DESC_DETAIL};

    private final Path indexBaseDir;
    private final CatalogProductRepository catalogProductRepository;
    private final LocalizedTextService textService;

    /** Analyzer per base language code (en, de, sv). */
    private final Map<String, Analyzer> analyzers = new HashMap<>();

    /** Open searcher per base language code. */
    private final Map<String, IndexSearcher> searchers = new HashMap<>();

    /** Open readers (kept for closing). */
    private final Map<String, DirectoryReader> readers = new HashMap<>();

    public LuceneSearchService(PostersProperties props,
                               CatalogProductRepository catalogProductRepository,
                               LocalizedTextService textService)
    {
        this.indexBaseDir = Path.of(props.getLuceneIndexDir());
        this.catalogProductRepository = catalogProductRepository;
        this.textService = textService;

        // Register language-specific analyzers (include stemmers + stop words)
        analyzers.put("en", new EnglishAnalyzer());
        analyzers.put("de", new GermanAnalyzer());
        analyzers.put("sv", new SwedishAnalyzer());
        analyzers.put("ja", new JapaneseAnalyzer());
    }

    /**
     * Returns the analyzer for the given base language code,
     * falling back to StandardAnalyzer for unknown languages.
     */
    private Analyzer analyzerFor(String baseLang)
    {
        return analyzers.getOrDefault(baseLang, new StandardAnalyzer());
    }

    /**
     * Extracts the base language code from a locale string (e.g. "en-US" → "en").
     */
    private String baseLang(String locale)
    {
        if (locale == null) return "en";
        int dash = locale.indexOf('-');
        return dash > 0 ? locale.substring(0, dash) : locale;
    }

    // ---------------------------------------------------------------
    // Index building (new entity model)
    // ---------------------------------------------------------------

    /**
     * Builds the Lucene index from all catalog products using the new entity model.
     * Each base language gets its own sub-directory under the index base.
     * Texts are resolved via LocalizedTextService using locale codes like "en-US", "de-DE", "sv-SE".
     *
     * <p>All localized texts are bulk-prefetched in a single DB query to avoid
     * the N+1 problem (~1,500 individual queries replaced by 1).</p>
     *
     * @param localeCodes  the locale codes to build indices for (e.g. {"en-US", "de-DE", "sv-SE"})
     */
    public void buildIndex(Set<String> localeCodes) throws IOException
    {
        List<Product> products = catalogProductRepository.findAll();

        // Derive base language codes from locale codes
        Map<String, String> baseLangToLocale = new HashMap<>();
        for (String locale : localeCodes)
        {
            String base = baseLang(locale);
            baseLangToLocale.putIfAbsent(base, locale);
        }
        // Always include English
        baseLangToLocale.putIfAbsent("en", "en-US");

        log.info("Building Lucene index for languages: {} ({} products)", baseLangToLocale.keySet(), products.size());

        // Bulk-prefetch ALL localized texts for the requested locales in one query
        Map<Integer, Map<String, String>> textCache =
            textService.getAllTextsForLocales(baseLangToLocale.values());

        // Close any previously open readers
        closeReaders();

        for (Map.Entry<String, String> entry : baseLangToLocale.entrySet())
        {
            String baseLang = entry.getKey();
            String locale = entry.getValue();

            Path langDir = indexBaseDir.resolve(baseLang);
            Files.createDirectories(langDir);

            Analyzer analyzer = analyzerFor(baseLang);
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE); // wipe + recreate

            try (FSDirectory dir = FSDirectory.open(langDir);
                 IndexWriter writer = new IndexWriter(dir, config))
            {
                for (Product product : products)
                {
                    Document doc = new Document();

                    // Stored field for retrieval
                    doc.add(new StoredField(FIELD_PRODUCT_ID, product.getId()));

                    // Look up texts from the prefetched cache instead of individual DB queries
                    String name = lookupText(textCache, product.getNameTextId(), locale);
                    String descOverview = lookupText(textCache, product.getDescriptionOverviewTextId(), locale);
                    String descDetail = lookupText(textCache, product.getDescriptionDetailTextId(), locale);

                    if (name != null && !name.isBlank())
                    {
                        doc.add(new TextField(FIELD_NAME, name, Field.Store.NO));
                    }
                    if (descOverview != null && !descOverview.isBlank())
                    {
                        doc.add(new TextField(FIELD_DESC_OVERVIEW, descOverview, Field.Store.NO));
                    }
                    if (descDetail != null && !descDetail.isBlank())
                    {
                        doc.add(new TextField(FIELD_DESC_DETAIL, descDetail, Field.Store.NO));
                    }

                    writer.addDocument(doc);
                }
                writer.commit();
                log.info("Indexed {} products for language '{}' (locale: {}).", products.size(), baseLang, locale);
            }
        }

        // Open readers for searching
        openReaders();
    }

    /**
     * Looks up a localized text from the prefetched cache, falling back to
     * en-US if the requested locale is not available.
     *
     * @param textCache the bulk-prefetched text map (textId → localeCode → text)
     * @param textId    the text ID to look up (may be null)
     * @param locale    the preferred locale code
     * @return the resolved text, or null if not found
     */
    private String lookupText(Map<Integer, Map<String, String>> textCache,
                              Integer textId, String locale)
    {
        if (textId == null) return null;

        Map<String, String> byLocale = textCache.get(textId);
        if (byLocale == null) return null;

        String text = byLocale.get(locale);
        if (text != null) return text;

        // Fallback to en-US
        return byLocale.get("en-US");
    }

    // ---------------------------------------------------------------
    // Reader management
    // ---------------------------------------------------------------

    /**
     * Opens DirectoryReader and IndexSearcher for each language sub-directory
     * that exists on disk. Called after import or on startup when import is skipped.
     */
    public void openReaders() throws IOException
    {
        closeReaders();

        if (!Files.isDirectory(indexBaseDir))
        {
            log.warn("Lucene index directory does not exist: {}. Search will return empty results.", indexBaseDir);
            return;
        }

        try (var dirs = Files.list(indexBaseDir))
        {
            for (Path langDir : dirs.filter(Files::isDirectory).toList())
            {
                String lang = langDir.getFileName().toString();
                try
                {
                    FSDirectory fsDir = FSDirectory.open(langDir);
                    DirectoryReader reader = DirectoryReader.open(fsDir);
                    readers.put(lang, reader);
                    searchers.put(lang, new IndexSearcher(reader));
                    log.info("Opened Lucene index for language '{}' ({} docs).", lang, reader.numDocs());
                }
                catch (IOException e)
                {
                    log.warn("Could not open Lucene index for language '{}': {}", lang, e.getMessage());
                }
            }
        }
    }

    private void closeReaders()
    {
        for (Map.Entry<String, DirectoryReader> entry : readers.entrySet())
        {
            try
            {
                entry.getValue().close();
            }
            catch (IOException e)
            {
                log.warn("Error closing Lucene reader for '{}': {}", entry.getKey(), e.getMessage());
            }
        }
        readers.clear();
        searchers.clear();
    }

    @Override
    public void destroy()
    {
        closeReaders();
        log.info("Lucene search service shut down.");
    }

    // ---------------------------------------------------------------
    // Searching
    // ---------------------------------------------------------------

    /**
     * Searches the Lucene index for the given locale and returns matching product IDs
     * in score order.
     *
     * @param queryText   the user's search query
     * @param locale      the current locale (e.g. "en-US", "de-DE")
     * @param maxResults  max number of results to return
     * @return list of product IDs ordered by relevance, empty list on error or no match
     */
    public List<Integer> search(String queryText, String locale, int maxResults)
    {
        String lang = baseLang(locale);
        IndexSearcher searcher = searchers.get(lang);
        Analyzer analyzer = analyzerFor(lang);

        if (searcher == null)
        {
            log.warn("No Lucene index available for language '{}'. Returning empty results.", lang);
            return List.of();
        }

        try
        {
            MultiFieldQueryParser parser = new MultiFieldQueryParser(SEARCH_FIELDS, analyzer);
            parser.setDefaultOperator(MultiFieldQueryParser.AND_OPERATOR);

            // Escape special Lucene characters and parse
            String escaped = MultiFieldQueryParser.escape(queryText.trim());
            if (escaped.isEmpty())
            {
                return List.of();
            }

            // Use prefix query to support partial matching (type-ahead)
            Query query = parser.parse(escaped + "*");

            TopDocs topDocs = searcher.search(query, maxResults);

            List<Integer> productIds = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs)
            {
                Document doc = searcher.doc(scoreDoc.doc);
                int productId = doc.getField(FIELD_PRODUCT_ID).numericValue().intValue();
                productIds.add(productId);
            }
            return productIds;
        }
        catch (Exception e)
        {
            log.error("Lucene search error for query '{}' in locale '{}': {}", queryText, locale, e.getMessage());
            return List.of();
        }
    }
}
