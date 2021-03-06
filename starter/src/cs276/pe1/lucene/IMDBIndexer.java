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

public class IMDBIndexer {
	
	public static void main(String[] argv) throws Exception {
		// where to store the index: you can choose your own location,
		// but be sure it is not in your pe1 folder (it should not be
		// submitted along with your code).  the lines below create
		// a new folder cs276-index in your home directory.
		File indexPath = new File(new File(System.getProperty("user.home")),"cs276-index");
		indexPath.mkdir();
		
		@SuppressWarnings("deprecation")
		IndexWriter writer = new IndexWriter(indexPath, new StandardAnalyzer(Version.LUCENE_CURRENT), true, IndexWriter.MaxFieldLength.LIMITED);

		for (MoviePlotRecord rec : IMDBParser.readRecords()) {
		
		    Document doc = new Document();
		    
		    Field title = new Field("title", rec.title, Field.Store.YES, Field.Index.ANALYZED);
		    Field plots = new Field("plots", rec.plots, Field.Store.YES, Field.Index.ANALYZED);
		    Field authors = new Field("authors", rec.authors, Field.Store.YES, Field.Index.ANALYZED);
		    
		    doc.add(title);
		    doc.add(plots);
		    doc.add(authors);
		
	        writer.addDocument(doc);
	        
			// TODO do indexing here
			// See IMDBParser.MoviePlotRecord for info on its fields
			// Be sure to add all the fields to the index.
		}
		writer.optimize();
		writer.close();
		
		// Can we make an external class?
		//Search("Rob", indexPath);
		
		//Hits hits = indexsearcher.search(query);
		System.err.println("Done");
	}
	
	
}
