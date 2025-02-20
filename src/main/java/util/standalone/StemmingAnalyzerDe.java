package util.standalone;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.de.GermanStemFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.Tokenizer;

public class StemmingAnalyzerDe extends Analyzer {
    @Override
      protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer source = new StandardTokenizer();
        return new TokenStreamComponents(source, new GermanStemFilter(new LowerCaseFilter(source)));
      }
}
