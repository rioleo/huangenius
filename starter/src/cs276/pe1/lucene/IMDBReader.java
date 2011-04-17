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


		// Access the file
		File indexPath = new File(new File(System.getProperty("user.home")),"cs276-index");		
		IndexReader ireader = IndexReader.open(indexPath, true);
		
		// Is there stuff in it?
		System.out.println(ireader.numDocs());
		IndexSearcher indexsearcher = new IndexSearcher(ireader);
		
		// Search within the title field
		//QueryParser queryparser = new QueryParser("author", new StandardAnalyzer());
		
		// Search for items that Rob has written
		//Query query = queryparser.parse("Rob");
		Term term = new Term("authors", "Rob");

		PhraseQuery query= new PhraseQuery();
		query.add(term);
		query.setSlop(0);
		
		
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
