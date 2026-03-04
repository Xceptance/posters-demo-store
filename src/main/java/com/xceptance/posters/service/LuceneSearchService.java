package com.xceptance.posters.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
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
import com.xceptance.posters.model.Language;
import com.xceptance.posters.model.Product;

/**
 * Manages per-language Lucene indices for full-text product search
 * with language-specific analyzers and stemmers.
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

    /** Analyzer per base language code (en, de, sv). */
    private final Map<String, Analyzer> analyzers = new HashMap<>();

    /** Open searcher per base language code. */
    private final Map<String, IndexSearcher> searchers = new HashMap<>();

    /** Open readers (kept for closing). */
    private final Map<String, DirectoryReader> readers = new HashMap<>();

    public LuceneSearchService(PostersProperties props)
    {
        this.indexBaseDir = Path.of(props.getLuceneIndexDir());

        // Register language-specific analyzers (include stemmers + stop words)
        analyzers.put("en", new EnglishAnalyzer());
        analyzers.put("de", new GermanAnalyzer());
        analyzers.put("sv", new SwedishAnalyzer());
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
    // Index building (called during data import)
    // ---------------------------------------------------------------

    /**
     * Builds the Lucene index from scratch for all languages.
     * Each base language gets its own sub-directory under the index base.
     *
     * @param products   all products to index
     * @param languages  all imported Language entities (used to derive base language codes)
     */
    public void buildIndex(List<Product> products, Collection<Language> languages) throws IOException
    {
        // Collect unique base language codes
        Set<String> baseLangs = languages.stream()
                .map(Language::getCode)
                .collect(Collectors.toSet());

        // Always include English as it is the default / original text language
        baseLangs.add("en");

        log.info("Building Lucene index for languages: {}", baseLangs);

        // Close any previously open readers
        closeReaders();

        for (String lang : baseLangs)
        {
            Path langDir = indexBaseDir.resolve(lang);
            Files.createDirectories(langDir);

            Analyzer analyzer = analyzerFor(lang);
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

                    // Resolve text for this language via the i18n system.
                    // DefaultText.getText(langCode) falls back to original text
                    // if no translation exists.
                    // We try with both the base code and common fallback codes.
                    String name = resolveText(product.getName(), lang);
                    String descOverview = resolveText(product.getDescriptionOverview(), lang);
                    String descDetail = resolveText(product.getDescriptionDetail(), lang);

                    if (name != null)
                    {
                        doc.add(new TextField(FIELD_NAME, name, Field.Store.NO));
                    }
                    if (descOverview != null)
                    {
                        doc.add(new TextField(FIELD_DESC_OVERVIEW, descOverview, Field.Store.NO));
                    }
                    if (descDetail != null)
                    {
                        doc.add(new TextField(FIELD_DESC_DETAIL, descDetail, Field.Store.NO));
                    }

                    writer.addDocument(doc);
                }
                writer.commit();
                log.info("Indexed {} products for language '{}'.", products.size(), lang);
            }
        }

        // Open readers for searching
        openReaders();
    }

    /**
     * Resolves the text for a DefaultText using the base language code.
     * Tries the base code first, then common fallback patterns.
     */
    private String resolveText(com.xceptance.posters.model.DefaultText dt, String baseLang)
    {
        if (dt == null) return null;
        // getText checks Language.code and Language.fallbackCode
        return dt.getText(baseLang);
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
