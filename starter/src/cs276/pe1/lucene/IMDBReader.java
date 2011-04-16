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

public class IMDBReader {
	
	public static void main(String[] argv) throws Exception {
		// where to store the index: you can choose your own location,
		// but be sure it is not in your pe1 folder (it should not be
		// submitted along with your code).  the lines below create
		// a new folder cs276-index in your home directory.
		File indexPath = new File(new File(System.getProperty("user.home")),"cs276-index");
		//indexPath.mkdir();
		
		
		IndexReader ireader = IndexReader.open(indexPath, true);
		System.out.println(ireader.numDocs());
		IndexSearcher indexsearcher = new IndexSearcher(ireader);
		QueryParser queryparser = new QueryParser("authors", new StandardAnalyzer());
		Query query = queryparser.parse("a");
		Hits hits = indexsearcher.search(query);
		int hitCount = hits.length();
		System.out.println(hitCount);
 		for (int i = 0; i < hitCount; i++) {
                Document doc = hits.doc(i);
                System.out.println("  " + (i + 1) + ". " + doc.get("title"));
            }
		
		//Hits hits = indexsearcher.search(query);
		System.err.println("Done");
	}
	
	
}
