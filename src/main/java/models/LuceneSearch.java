package models;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.QueryBuilder;

import io.ebean.DB;
import ninja.lifecycle.Start;
import util.standalone.StemmingAnalyzer;
import util.standalone.StemmingAnalyzerDe;
import util.standalone.StemmingAnalyzerFr;

public class LuceneSearch implements SearchEngine {
    // setup default english analyzer
    Analyzer prodAna = new StemmingAnalyzer();

    // setup other language analyzers
    Analyzer prodAnaDe = new StemmingAnalyzerDe();
    Analyzer prodAnaFr = new StemmingAnalyzerFr();

    // setup directory for default index - for now in memory, alternative Option might be desirable
    final Directory prodIndex = new ByteBuffersDirectory();

    // setup directory for non default indeces - for now in memory, alternative Option might be desirable
    final Directory prodIndexDe = new ByteBuffersDirectory();
    final Directory prodIndexFr = new ByteBuffersDirectory();

    // standard constructor that sets up used analyzer as a standard analyzer with default language (english) stemming
    public LuceneSearch() { 
        prodAna = new StemmingAnalyzer();
    }

    @Start(order = 100)
    public void firstIndexing() {
        setup();
    }

    @Override
    public void setup() {
        // index default language (english)
        indexData();
        // index other languages
        indexDataDe();
    }

    @Override
    public List<Integer> search(String searchText, int maxNumberOfHits) {
        List<Integer> results = new ArrayList<Integer>();
        // setup query builder
        QueryBuilder builder = new QueryBuilder(prodAna);
        // setup queries
        BooleanQuery.Builder fullQuery = new BooleanQuery.Builder();
        // check names
        Query queryN = builder.createBooleanQuery("name", searchText, BooleanClause.Occur.MUST);
        fullQuery.add(queryN, BooleanClause.Occur.SHOULD);
        // check short description
        Query querySD = builder.createBooleanQuery("overview", searchText, BooleanClause.Occur.MUST);
        fullQuery.add(querySD, BooleanClause.Occur.SHOULD);
        // check long description
        Query queryLD = builder.createBooleanQuery("description", searchText, BooleanClause.Occur.MUST);
        fullQuery.add(queryLD, BooleanClause.Occur.SHOULD);
        // setup index reader and searcher and perform search
        try {
            // setup
            IndexReader reader = DirectoryReader.open(prodIndex);
            IndexSearcher searcher = new IndexSearcher(reader);
            // search
            TopDocs topDocs = searcher.search(fullQuery.build(), maxNumberOfHits);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : hits) {
                results.add(Integer.parseInt(searcher.doc(scoreDoc.doc).get("id")));
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        
        return results;
    }

    @Override
    public List<Integer> search(String searchText, int maxNumberOfHits, String locale) {
        List<Integer> results = new ArrayList<Integer>();
        // setup query builder
        QueryBuilder builder;
        if (locale.equals("de-DE")) {
            builder = new QueryBuilder(prodAnaDe);
        }
        else if (locale.equals("fr-FR")) {
            builder = new QueryBuilder(prodAnaFr);
        }
        else if (locale.equals("fr-CA")) {
            builder = new QueryBuilder(prodAnaFr);
        }
        else if (locale.equals("en-UK")) {
            builder = new QueryBuilder(prodAna);
        }
        else {
            builder = new QueryBuilder(prodAna);
        }
        // setup queries
        BooleanQuery.Builder fullQuery = new BooleanQuery.Builder();
        // check names
        Query queryN = builder.createBooleanQuery("name", searchText, BooleanClause.Occur.MUST);
        fullQuery.add(queryN, BooleanClause.Occur.SHOULD);
        // check short description
        Query querySD = builder.createBooleanQuery("overview", searchText, BooleanClause.Occur.MUST);
        fullQuery.add(querySD, BooleanClause.Occur.SHOULD);
        // check long description
        Query queryLD = builder.createBooleanQuery("description", searchText, BooleanClause.Occur.MUST);
        fullQuery.add(queryLD, BooleanClause.Occur.SHOULD);
        // setup index reader and searcher and perform search
        try {
            // setup
            IndexReader reader;
            if (locale.equals("de-DE")) {
                reader = DirectoryReader.open(prodIndexDe);
            }
            else if (locale.equals("fr-FR")) {
                reader = DirectoryReader.open(prodIndexFr);
            }
            else if (locale.equals("fr-CA")) {
                reader = DirectoryReader.open(prodIndexFr);
            }
            else if (locale.equals("en-UK")) {
                reader = DirectoryReader.open(prodIndex);
            }
            else {
                reader = DirectoryReader.open(prodIndex);
            }
            IndexSearcher searcher = new IndexSearcher(reader);
            // search
            TopDocs topDocs = searcher.search(fullQuery.build(), maxNumberOfHits);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : hits) {
                results.add(Integer.parseInt(searcher.doc(scoreDoc.doc).get("id")));
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        
        return results;
    }

    // currently indexes only products
    private void indexData() {
        
        // setup index writer + config
        IndexWriterConfig config = new IndexWriterConfig(prodAna);
        try {
            IndexWriter wri = new IndexWriter(prodIndex, config);
            // loop through all products to add to the index
            List<Product> products = getAllProducts();
            for (Product product : products) {
                // create a 'document' (= an indexing target)
                Document prodDoc = new Document();
                // Setup the fields in that document, we store the id so we can use it to retrieve search results
                StoredField prodId = new StoredField("id", product.getId());
                TextField prodName = new TextField("name", product.getDefaultName(), Store.NO);
                TextField prodShortDesc = new TextField("overview", product.getDefaultDescriptionOverview(), Store.NO);
                TextField prodLongDesc = new TextField("description", product.getDefaultDescriptionDetail(), Store.NO);
                prodDoc.add(prodId);
                prodDoc.add(prodName);
                prodDoc.add(prodShortDesc);
                prodDoc.add(prodLongDesc);
                // add the document with the products information to the index writer
                wri.addDocument(prodDoc);
            }  
            wri.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    // currently indexes only products
    private void indexDataDe() {
      
        // setup index writer + config
        IndexWriterConfig config = new IndexWriterConfig(prodAnaDe);
        try {
            IndexWriter wri = new IndexWriter(prodIndexDe, config);
            // loop through all products to add to the index
            List<Product> products = getAllProducts();
            for (Product product : products) {
                // create a 'document' (= an indexing target)
                Document prodDoc = new Document();
                // Setup the fields in that document, we store the id so we can use it to retrieve search results
                StoredField prodId = new StoredField("id", product.getId());
                TextField prodName = new TextField("name", product.getName("de-DE"), Store.NO);
                TextField prodShortDesc = new TextField("overview", product.getDescriptionOverview("de-DE"), Store.NO);
                TextField prodLongDesc = new TextField("description", product.getDescriptionDetail("de-DE"), Store.NO);
                prodDoc.add(prodId);
                prodDoc.add(prodName);
                prodDoc.add(prodShortDesc);
                prodDoc.add(prodLongDesc);
                // add the document with the products information to the index writer
                wri.addDocument(prodDoc);
            }  
            wri.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    // currently indexes only products
    private void indexDataFr() {
      
        // setup index writer + config
        IndexWriterConfig config = new IndexWriterConfig(prodAnaFr);
        try {
            IndexWriter wri = new IndexWriter(prodIndexFr, config);
            // loop through all products to add to the index
            List<Product> products = getAllProducts();
            for (Product product : products) {
                // create a 'document' (= an indexing target)
                Document prodDoc = new Document();
                // Setup the fields in that document, we store the id so we can use it to retrieve search results
                StoredField prodId = new StoredField("id", product.getId());
                // TODO change these to get the french texts and not the default
                TextField prodName = new TextField("name", product.getDefaultName(), Store.NO);
                TextField prodShortDesc = new TextField("overview", product.getDefaultDescriptionOverview(), Store.NO);
                TextField prodLongDesc = new TextField("description", product.getDefaultDescriptionDetail(), Store.NO);
                prodDoc.add(prodId);
                prodDoc.add(prodName);
                prodDoc.add(prodShortDesc);
                prodDoc.add(prodLongDesc);
                // add the document with the products information to the index writer
                wri.addDocument(prodDoc);
            }  
            wri.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    private List<Product> getAllProducts() {
        return DB.find(Product.class).findList();
    }
}
