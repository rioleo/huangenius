package cs276.pe1.spell;

import java.io.File;
import java.util.List;

import cs276.util.IOUtils;
import cs276.util.StringUtils;
import java.util.*;

import cs276.util.Counter;

public class KGramSpellingCorrector implements SpellingCorrector {
	/** Initializes spelling corrector by indexing kgrams in words from a file */
	
	private HashMap<String, Counter<String>> index;
	
	public KGramSpellingCorrector() {
	    
	    // instantiate new index
	    index = new HashMap<String, Counter<String>>();
	    
        File path = new File("/afs/ir/class/cs276/pe1-2011/big.txt.gz");
        for (String line : IOUtils.readLines(IOUtils.openFile(path))) {
            for (String word : StringUtils.tokenize(line)) {
                ArrayList<String> bigrams = getBigrams(word);
                
                for (String bigram : bigrams) {
                    if (index.containsKey(bigram)) {
                        index.get(bigram).incrementCount(word);
                    } else {
                        Counter<String> words = new Counter<String>();
                        words.incrementCount(word);
                        index.put(bigram, words);
                    }
                }
                
//                for (String key : index.keySet()) {
//                    System.out.println(key + " " + index.get(key));
//                }
                
            }
        }
	}

	public List<String> corrections(String word) {
		Set<String> wordBigrams = getBigramsSet(word);
		
		Counter<String> possibleCorrections = new Counter<String>();
		
		for (String wordBigram : wordBigrams) {
		    Set<String> postings = null;
		    if (index.containsKey(wordBigram)) postings = index.get(wordBigram).keySet();
		    if (postings != null) {
		        for (String posting : postings) {
		            if (!possibleCorrections.containsKey(posting)) {
	                    Set<String> intersect = getBigramsSet(posting);
                        intersect.retainAll(wordBigrams);
	                    
	                    Set<String> union = getBigramsSet(posting);
	                    union.addAll(wordBigrams);
	                    
//	                    System.out.println(word + " " + posting + ": " + intersect);
	                    
	                    possibleCorrections.setCount(posting, ((double) intersect.size())/union.size());
		                
	                }
		        }
		    }
		}
		
	    System.out.println("Word: " + word);
	    for (String entry : possibleCorrections.topK(10)) {
	        System.out.println(entry + " " + possibleCorrections.getCount(entry));
	    }
		
		return possibleCorrections.topK(10);
		
	}
	
	
	private Set<String> getBigramsSet(String word) {
	    Set<String> bigrams = new HashSet<String>();
	    word = "$" + word + "$";
        for (int i = 0; i < word.length()-1; i++) {
            String bigram = word.substring(i, i+2);
            bigrams.add(bigram);
        }
        return bigrams;
	}
	
	private ArrayList<String> getBigrams(String word) {
	    ArrayList<String> bigrams = new ArrayList<String>();
	    word = "$" + word + "$";
        for (int i = 0; i < word.length()-1; i++) {
            String bigram = word.substring(i, i+2);
            bigrams.add(bigram);
        }
        return bigrams;
	}
	
}
