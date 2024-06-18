package models;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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

import io.ebean.Ebean;

public class LuceneSearch implements SearchEngine {
    // setup analyzer
    Analyzer prodAna;
    // setup directory - for now in memory, alternative Option might be desirable
    final Directory prodIndex = new ByteBuffersDirectory();

    // standard constructor that sets up used analyzer as a standard analyzer
    public LuceneSearch() { 
        prodAna = new StandardAnalyzer();
        setup();
    }

    @Override
    public void setup() {
        indexData();
    }

    @Override
    public List<Integer> search(String searchText, int maxNumberOfHits) {
        List<Integer> results = new ArrayList<Integer>();
        // setup query builder
        QueryBuilder builder = new QueryBuilder(prodAna);
        // setup queries
        BooleanQuery.Builder fullQuery = new BooleanQuery.Builder();
        // check names
        Query queryN = builder.createBooleanQuery("name", searchText);
        fullQuery.add(queryN, BooleanClause.Occur.SHOULD);
        // check short description
        Query querySD = builder.createBooleanQuery("overview", searchText);
        fullQuery.add(querySD, BooleanClause.Occur.SHOULD);
        // check long description
        Query queryLD = builder.createBooleanQuery("description", searchText);
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
                TextField prodName = new TextField("name", product.getName(), Store.NO);
                TextField prodShortDesc = new TextField("overview", product.getDescriptionOverview(), Store.NO);
                TextField prodLongDesc = new TextField("description", product.getDescriptionDetail(), Store.NO);
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
        return Ebean.find(Product.class).findList();
    }
}
