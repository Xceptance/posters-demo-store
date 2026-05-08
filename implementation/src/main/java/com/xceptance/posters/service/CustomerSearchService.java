/*
 * Copyright 2026 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xceptance.posters.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.xceptance.posters.config.PostersProperties;
import com.xceptance.posters.entity.Customer;
import com.xceptance.posters.repository.CustomerRepository;

/**
 * Asynchronous Lucene-based search indexer for {@link Customer} entities.
 *
 * <p>Uses a bounded queue (fast path) and a bulk-rebuild fallback (slow path)
 * to decouple web request latency from search indexing. The fast path passes
 * customer IDs directly; when the queue overflows, the slow path queries the
 * database for all customers and rebuilds the index in bulk.</p>
 *
 * <p>Indexed fields: customer number (exact + sortable), email (exact + text),
 * first name (text), last name (text).</p>
 *
 * <p>Created exclusively by AI (Claude Opus 4.6).</p>
 */
@Service
public class CustomerSearchService
{
    private static final Logger log = LoggerFactory.getLogger(CustomerSearchService.class);

    // --- Lucene field name constants ---
    private static final String FIELD_ID = "id";
    private static final String FIELD_NUMBER = "customerNumber";
    private static final String FIELD_NUMBER_STR = "customerNumber_str";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_EMAIL_TEXT = "email_text";
    private static final String FIELD_FIRST_NAME = "firstName";
    private static final String FIELD_LAST_NAME = "lastName";

    /** Fields queried by the multi-field parser. */
    private static final String[] SEARCH_FIELDS =
    {
        FIELD_NUMBER_STR, FIELD_EMAIL_TEXT, FIELD_FIRST_NAME, FIELD_LAST_NAME
    };

    /** Maximum number of pending index updates before falling back to bulk rebuild. */
    private static final int QUEUE_CAPACITY = 1000;

    private final Path indexDir;
    private final CustomerRepository customerRepository;
    private final Analyzer analyzer = new StandardAnalyzer();

    // Mutable Lucene state – guarded by synchronized refreshReader()
    private Directory directory;
    private DirectoryReader reader;
    private IndexSearcher searcher;

    /**
     * Size-limited queue for fast-path indexing. Each entry is a customer UUID
     * that needs to be re-indexed or removed from the index.
     */
    private final BlockingQueue<UUID> dirtyQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    /**
     * When {@code true}, the next scheduled run will perform a full rebuild
     * instead of processing individual queue entries. Set when the queue overflows.
     */
    private final AtomicBoolean needsBulkRebuild = new AtomicBoolean(false);

    /**
     * Constructs the service, resolving the index directory from application properties.
     *
     * @param props              application configuration containing the Lucene base directory
     * @param customerRepository repository for loading customer data
     */
    public CustomerSearchService(final PostersProperties props,
                                 final CustomerRepository customerRepository)
    {
        this.indexDir = Path.of(props.getLuceneIndexDir()).resolve("customers");
        this.customerRepository = customerRepository;
    }

    /**
     * Initialises the Lucene directory and creates an empty index if none exists.
     * Called automatically by Spring after bean construction.
     */
    @PostConstruct
    public void init()
    {
        try
        {
            Files.createDirectories(indexDir);
            directory = FSDirectory.open(indexDir);

            // Bootstrap an empty index so DirectoryReader.open() never fails
            if (!DirectoryReader.indexExists(directory))
            {
                final IndexWriterConfig config = new IndexWriterConfig(analyzer);
                try (final IndexWriter writer = new IndexWriter(directory, config))
                {
                    writer.commit();
                }
                // The index was just created, so schedule a bulk rebuild from the database
                needsBulkRebuild.set(true);
            }
            refreshReader();
        }
        catch (final IOException e)
        {
            log.error("Failed to initialise customer Lucene index at {}", indexDir, e);
        }
    }

    /**
     * Closes all Lucene resources. Called automatically by Spring on shutdown.
     */
    @PreDestroy
    public void destroy()
    {
        try
        {
            if (reader != null)
            {
                reader.close();
            }
            if (directory != null)
            {
                directory.close();
            }
        }
        catch (final IOException e)
        {
            log.error("Error closing customer index", e);
        }
    }

    // ------------------------------------------------------------------
    // Reader lifecycle
    // ------------------------------------------------------------------

    /**
     * Refreshes the {@link DirectoryReader} and re-creates the {@link IndexSearcher}.
     * If the reader is already open, uses {@code openIfChanged} to avoid
     * unnecessary I/O.
     *
     * @throws IOException if the reader cannot be opened
     */
    private synchronized void refreshReader() throws IOException
    {
        if (reader == null)
        {
            reader = DirectoryReader.open(directory);
        }
        else
        {
            final DirectoryReader newReader = DirectoryReader.openIfChanged(reader);
            if (newReader != null)
            {
                reader.close();
                reader = newReader;
            }
        }
        searcher = new IndexSearcher(reader);
    }

    // ------------------------------------------------------------------
    // Async indexing – public API
    // ------------------------------------------------------------------

    /**
     * Enqueues a single customer for asynchronous re-indexing (fast path).
     * If the queue is full, sets the bulk-rebuild flag so the next scheduled
     * run rebuilds the entire index from the database (slow path).
     *
     * @param customerId UUID of the customer to re-index
     */
    public void indexCustomerAsync(final UUID customerId)
    {
        if (!dirtyQueue.offer(customerId))
        {
            needsBulkRebuild.set(true);
            log.warn("Customer index queue full – falling back to bulk rebuild.");
        }
    }

    // ------------------------------------------------------------------
    // Scheduled processing
    // ------------------------------------------------------------------

    /**
     * Scheduled job that drains the dirty queue and updates the Lucene index.
     * Runs every 5 seconds. If a bulk rebuild was requested (queue overflow),
     * delegates to {@link #triggerFullIndex()} instead.
     */
    @Scheduled(fixedDelay = 5000)
    public void processIndexQueue()
    {
        if (needsBulkRebuild.getAndSet(false))
        {
            triggerFullIndex();
            return;
        }

        final List<UUID> toProcess = new ArrayList<>();
        dirtyQueue.drainTo(toProcess);

        if (toProcess.isEmpty())
        {
            return;
        }

        log.debug("Processing {} customers for indexing...", toProcess.size());

        try (final IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer)))
        {
            for (final UUID id : toProcess)
            {
                customerRepository.findById(id).ifPresentOrElse(
                    c ->
                    {
                        try
                        {
                            writer.updateDocument(
                                new Term(FIELD_ID, c.getId().toString()),
                                createDocument(c));
                        }
                        catch (final IOException e)
                        {
                            log.error("Error updating customer {} in index", id, e);
                        }
                    },
                    () ->
                    {
                        try
                        {
                            writer.deleteDocuments(new Term(FIELD_ID, id.toString()));
                        }
                        catch (final IOException e)
                        {
                            log.error("Error deleting customer {} from index", id, e);
                        }
                    }
                );
            }
            writer.commit();
            refreshReader();
        }
        catch (final Exception e)
        {
            log.error("Error processing customer index queue", e);
            needsBulkRebuild.set(true);
        }
    }

    /**
     * Performs a complete rebuild of the customer search index from the database.
     * Called on startup after seed-data import and as a fallback when the
     * fast-path queue overflows.
     */
    @Async
    public void triggerFullIndex()
    {
        log.info("Triggering full rebuild of customer search index...");

        final IndexWriterConfig config = new IndexWriterConfig(analyzer)
            .setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        try (final IndexWriter writer = new IndexWriter(directory, config))
        {
            final List<Customer> customers = customerRepository.findAll();
            for (final Customer c : customers)
            {
                writer.addDocument(createDocument(c));
            }
            writer.commit();
            refreshReader();
            log.info("Finished indexing {} customers.", customers.size());
        }
        catch (final Exception e)
        {
            log.error("Error during full customer index rebuild", e);
        }
    }

    // ------------------------------------------------------------------
    // Document creation
    // ------------------------------------------------------------------

    /**
     * Converts a {@link Customer} entity into a Lucene {@link Document}.
     *
     * <p>Field strategy:</p>
     * <ul>
     *   <li>{@code id} – stored only, used for retrieval</li>
     *   <li>{@code customerNumber} – LongPoint (range queries), NumericDocValues (sorting),
     *       StringField (exact text match)</li>
     *   <li>{@code email} – StringField (exact), TextField (tokenised partial match)</li>
     *   <li>{@code firstName}, {@code lastName} – TextField (tokenised)</li>
     * </ul>
     *
     * @param customer the customer entity to index
     * @return a Lucene document ready for indexing
     */
    private Document createDocument(final Customer customer)
    {
        final Document doc = new Document();
        doc.add(new StoredField(FIELD_ID, customer.getId().toString()));

        if (customer.getCustomerNumber() != null)
        {
            doc.add(new LongPoint(FIELD_NUMBER, customer.getCustomerNumber()));
            doc.add(new NumericDocValuesField(FIELD_NUMBER, customer.getCustomerNumber()));
            doc.add(new StringField(FIELD_NUMBER_STR,
                customer.getCustomerNumber().toString(), Field.Store.NO));
        }

        if (customer.getEmail() != null)
        {
            doc.add(new StringField(FIELD_EMAIL,
                customer.getEmail().toLowerCase(), Field.Store.NO));
            doc.add(new TextField(FIELD_EMAIL_TEXT,
                customer.getEmail(), Field.Store.NO));
        }

        if (customer.getFirstName() != null)
        {
            doc.add(new TextField(FIELD_FIRST_NAME,
                customer.getFirstName(), Field.Store.NO));
        }

        if (customer.getLastName() != null)
        {
            doc.add(new TextField(FIELD_LAST_NAME,
                customer.getLastName(), Field.Store.NO));
        }

        return doc;
    }

    // ------------------------------------------------------------------
    // Search
    // ------------------------------------------------------------------

    /**
     * Searches the customer index for the given query text and returns
     * a page of matching customer UUIDs sorted by customer number descending.
     *
     * <p>An empty or blank query matches all documents (useful for the
     * unpaginated list view).</p>
     *
     * @param queryText free-text query; may be {@code null} or blank
     * @param offset    zero-based offset for pagination
     * @param limit     maximum number of results to return
     * @return search result containing total hit count and customer IDs
     */
    public CustomerSearchResult search(final String queryText,
                                       final int offset,
                                       final int limit)
    {
        if (searcher == null)
        {
            return new CustomerSearchResult(0, List.of());
        }

        try
        {
            final Query query;
            if (queryText == null || queryText.isBlank())
            {
                query = new MatchAllDocsQuery();
            }
            else
            {
                final MultiFieldQueryParser parser =
                    new MultiFieldQueryParser(SEARCH_FIELDS, analyzer);
                parser.setDefaultOperator(MultiFieldQueryParser.AND_OPERATOR);
                final String escaped = MultiFieldQueryParser.escape(queryText.trim());
                query = parser.parse(escaped + "*");
            }

            // Sort by customer number descending (newest first)
            final Sort sort = new Sort(
                new SortField(FIELD_NUMBER, SortField.Type.LONG, true));

            final int maxDocs = Math.max(1, offset + limit);
            final TopFieldDocs topDocs = searcher.search(query, maxDocs, sort);

            final List<UUID> ids = new ArrayList<>();
            final ScoreDoc[] hits = topDocs.scoreDocs;
            final int end = Math.min(hits.length, offset + limit);

            for (int i = offset; i < end; i++)
            {
                final Document doc = searcher.doc(hits[i].doc);
                ids.add(UUID.fromString(doc.getField(FIELD_ID).stringValue()));
            }

            return new CustomerSearchResult(topDocs.totalHits.value, ids);
        }
        catch (final Exception e)
        {
            log.error("Customer search error for query '{}'", queryText, e);
            return new CustomerSearchResult(0, List.of());
        }
    }

    // ------------------------------------------------------------------
    // Result DTO
    // ------------------------------------------------------------------

    /**
     * Immutable result container for customer search operations.
     *
     * @param totalHits   total number of documents matching the query
     * @param customerIds ordered list of customer UUIDs for the requested page
     */
    public record CustomerSearchResult(long totalHits, List<UUID> customerIds)
    {
    }
}
