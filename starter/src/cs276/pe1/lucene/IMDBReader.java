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
import cs276.util.Counter;
import cs276.pe1.spell.KGramWithEditDistanceSpellingCorrector;
import cs276.util.StringUtils;

public class IMDBReader {
	
	static KGramWithEditDistanceSpellingCorrector spellChecker = new KGramWithEditDistanceSpellingCorrector();
	
	public static String runQueryForTitle(String rawQuery, String field) throws Exception {
		
		File indexPath = new File(new File(System.getProperty("user.home")),"cs276-index");		
		IndexReader ireader = IndexReader.open(indexPath);
		IndexSearcher indexsearcher = new IndexSearcher(ireader);
		
		//Our spellchecker
        QueryParser queryParser = new QueryParser(field,new StandardAnalyzer());
        List<String> corrections = spellChecker.corrections(rawQuery);
		
		//Lucene spellchecker
		System.out.println("******Lucene spellchecker suggestions");
		FSDirectory fs = FSDirectory.getDirectory(indexPath);
		SpellChecker spell = new SpellChecker(fs);
		
		//Implementation 1. 
		String[] similar = spell.suggestSimilar(rawQuery, 1);	
		for (String word : similar) {
			System.out.println(word);
		}
		
		//Implementation 2.
		//System.out.println("----> with morePopular = true <--");
		//String[] similar = spell.suggestSimilar(rawQuery, 1, ireader, field, true);
		//for (String word : similar) {
		//		    System.out.println(word);
		//	    }
		//System.out.println("----> with morePopular = false <--");
		//similar = spell.suggestSimilar(rawQuery, 1, ireader, field, false);
		//for (String word : similar) {
		//	System.out.println(word);
		//}
		
		//Implementation 3. Uncomment this section
		
		//Counter<String> editDistances = new Counter<String>();
		//for (String guess : similar) {
	    //    editDistances.setCount(guess, -1*StringUtils.levenshtein(rawQuery, guess));
        //}
		//System.out.println("---->Edit distance on Lucene<----");
		//System.out.println(editDistances.topK(1));
		
        if (corrections != null && corrections.size() > 0 && !corrections.get(0).equals(rawQuery)) {
            rawQuery = corrections.get(0);
            System.out.println("******Spellchecker: searching for " + rawQuery);
        }
        
        Query query = queryParser.parse(rawQuery);
		
		TopDocs results = indexsearcher.search(query, null, 20);
		for (ScoreDoc doc : results.scoreDocs) {
		    System.out.println(ireader.document(doc.doc).get("title"));
	    }
				
		
        System.out.println("Query: " + query);
		System.out.println("Results: showing " + results.scoreDocs.length + " out of " + results.totalHits);
        return "";
		
	}
	
	
	public static void main(String[] argv) throws Exception {
		
		
		File indexPath = new File(new File(System.getProperty("user.home")),"cs276-index");		
		IndexReader ireader = IndexReader.open(indexPath);
		
		// Is there stuff in it?
		System.out.println("Total docs: " + ireader.numDocs());
		
		//		System.out.println(ireader.document(100));
		
		IndexSearcher indexsearcher = new IndexSearcher(ireader);
		
		
		
		
		
		// Meh
		//PhraseQuery query = new PhraseQuery();
		//query.add(new Term("title","10"));
		//query.add(new Term("title","items"));
		
		// WORKS
		//QueryParser queryParser = new QueryParser("title",new StandardAnalyzer());
		//Query query = queryParser.parse("\"10 items or less\"~1");
		
		// This WORKS
		//PhraseQuery query = new PhraseQuery();
		//query.setSlop(5);
		//query.add(new Term("plots","eighteen"));
		//query.add(new Term("plots","murdered"));
		
		
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
		
		QueryParser queryParser = new QueryParser("title",new StandardAnalyzer());
		Query query = queryParser.parse("authors:Rob^4 authors:Hart");
		
		TopDocs results = indexsearcher.search(query, null, 20);
		
		
		for (ScoreDoc doc : results.scoreDocs) {
		    System.out.println(ireader.document(doc.doc));
	    }
		
		
        System.out.println("Query: " + query);
		System.out.println("Results: " + results.totalHits);
		
	}
	
	
}
