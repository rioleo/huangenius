package cs276.pe1.lucene;

import java.io.File;

import org.apache.lucene.search.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.util.Version;

import cs276.pe1.lucene.IMDBParser.MoviePlotRecord;
import org.apache.lucene.document.*;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

public class IMDBReader {
	
	public static void main(String[] argv) throws Exception {


		File indexPath = new File(new File(System.getProperty("user.home")),"cs276-index");		
		IndexReader ireader = IndexReader.open(indexPath);
		
		// Is there stuff in it?
		System.out.println("Total docs: " + ireader.numDocs());
		
//		System.out.println(ireader.document(100));
		
		IndexSearcher indexsearcher = new IndexSearcher(ireader);
		

		


// 10 items query works
        Term term = new Term("title", "Trmmy");
		PrefixQuery query = new PrefixQuery(term);

// Murdered eighteen query DOES NOT WORK
//        Term term1 = new Term("plots", "murdered");
//        Term term2 = new Term("plots", "eighteen");
//        PhraseQuery query = new PhraseQuery();
//        query.add(term1);
//        query.add(term2);
//        query.setSlop(5);


// Rob query WORKS
		//Term term = new Term("authors", "Rob");
        //TermQuery query = new TermQuery(term);		


		TopDocs results = indexsearcher.search(query, null, 20);

		
		for (ScoreDoc doc : results.scoreDocs) {
		    System.out.println(ireader.document(doc.doc));
	    }
		
		
//		Hits hits = indexsearcher.search(query);
//		int hitCount = hits.length();
//		System.out.println("Results: " + hitCount);
// 		for (int i = 0; i < hitCount; i++) {
//                Document doc = hits.doc(i);
//                System.out.println("  " + (i + 1) + ". " + doc.get("title"));
//            }
//		
		//Hits hits = indexsearcher.search(query);
        System.out.println("Query: " + query);
		System.out.println("Results: " + results.totalHits);
//		System.err.println("Done");
	
	}
	
	
}
