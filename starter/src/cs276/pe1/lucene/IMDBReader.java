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

import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.search.spell.LuceneDictionary;
import java.util.List;
import org.apache.lucene.store.FSDirectory;

import cs276.pe1.spell.KGramWithEditDistanceSpellingCorrector;

public class IMDBReader {
	
	static KGramWithEditDistanceSpellingCorrector spellChecker = new KGramWithEditDistanceSpellingCorrector();
	
	public static String runQueryForTitle(String rawQuery) throws Exception {
		
		File indexPath = new File(new File(System.getProperty("user.home")),"cs276-index");		
//		IndexReader ireader = IndexReader.open(indexPath);
//		IndexSearcher indexsearcher = new IndexSearcher(ireader);
		
//        QueryParser queryParser = new QueryParser("title",new StandardAnalyzer());
//        List<String> corrections = spellChecker.corrections(rawQuery);
		
		System.out.println("******Lucene spellchecker suggestions");
		FSDirectory fs = FSDirectory.getDirectory(indexPath);
		SpellChecker spell = new SpellChecker(fs);
//		spell.indexDictionary(new LuceneDictionary(ireader,"title"));
//		spell.indexDictionary(new LuceneDictionary(ireader,"authors"));
//		spell.indexDictionary(new LuceneDictionary(ireader,"plots"));
		String[] similar = spell.suggestSimilar(rawQuery, 5);	
		for (String word : similar) {
	        System.out.println(word);
	    }
		
//        if (corrections != null && corrections.size() > 0 && !corrections.get(0).equals(rawQuery)) {
//            rawQuery = corrections.get(0);
//            System.out.println("******Spellchecker: searching for " + rawQuery);
//        }
        
//        Query query = queryParser.parse(rawQuery);
//		
//		TopDocs results = indexsearcher.search(query, null, 20);
//		for (ScoreDoc doc : results.scoreDocs) {
//		    System.out.println(ireader.document(doc.doc).get("title"));
//	    }
//				
//		
//        System.out.println("Query: " + query);
//		System.out.println("Results: showing " + results.scoreDocs.length + " out of " + results.totalHits);
        return "";
		
	}
	
	
	public static void main(String[] argv) throws Exception {
		
		
		File indexPath = new File(new File(System.getProperty("user.home")),"cs276-index");		
		IndexReader ireader = IndexReader.open(indexPath);
		
		// Is there stuff in it?
		System.out.println("Total docs: " + ireader.numDocs());
		
		//		System.out.println(ireader.document(100));
		
		IndexSearcher indexsearcher = new IndexSearcher(ireader);
		
		
		

		
		// WORKS
		QueryParser queryParser = new QueryParser("title",new StandardAnalyzer());
		Query query = queryParser.parse("\"10 items or less\"~1");
		
		// This WORKS
//		PhraseQuery query = new PhraseQuery();
//		query.setSlop(5);
//		query.add(new Term("plots","eighteen"));
//		query.add(new Term("plots","murdered"));
		
		
		// Rob query WORKS
		//QueryParser queryParser = new QueryParser("authors",new StandardAnalyzer());
		//Query query = queryParser.parse("Rob");
		
		// Search
		//FSDirectory fs = FSDirectory.getDirectory(indexPath);
		//SpellChecker spell = new SpellChecker(fs);
		//spell.indexDictionary(new LuceneDictionary(ireader,"title"));
		//String[] similar = spell.suggestSimilar("Trmmy", 10);	
		
		//for (String word : similar) {
		//		    System.out.println(word);
		//	    }
		
//		QueryParser queryParser = new QueryParser("title",new StandardAnalyzer());
//		
		
		TopDocs results = indexsearcher.search(query, null, 20);
		
		
		for (ScoreDoc doc : results.scoreDocs) {
		    System.out.println(ireader.document(doc.doc));
	    }
		
		
        System.out.println("Query: " + query);
		System.out.println("Results: " + results.totalHits);
		
	}
	
	
}
