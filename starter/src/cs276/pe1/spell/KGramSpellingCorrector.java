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
//		File path = new File("/afs/ir/class/cs276/pe1-2011/imdb-plots-20081003.list.gz");
        for (String line : IOUtils.readLines(IOUtils.openFile(path))) {        
            for (String word : StringUtils.tokenize(line)) {
            
                //ArrayList<String> bigrams = getBigrams(word);           
				ArrayList<String> bigrams = getKgrams(word, 2); 
                for (String bigram : bigrams) {
                    if (index.containsKey(bigram)) {
                        index.get(bigram).incrementCount(word);
                    } else {
                        Counter<String> words = new Counter<String>();
                        words.incrementCount(word);
                        index.put(bigram, words);
                    }
                }
                
            }
        }
	}

	public List<String> corrections(String word) {
		Set<String> wordBigrams = getKgramsSet(word, 2);
		
		Counter<String> possibleCorrections = new Counter<String>();
		
		for (String wordBigram : wordBigrams) {
		    Set<String> postings = null;
		    if (index.containsKey(wordBigram)) postings = index.get(wordBigram).keySet();
		    if (postings != null) {
		        for (String posting : postings) {
		            if (!possibleCorrections.containsKey(posting)) {
	                    Set<String> intersect = getKgramsSet(posting, 2);
                        intersect.retainAll(wordBigrams);
	                    
	                    Set<String> union = getKgramsSet(posting, 2);
	                    union.addAll(wordBigrams);
	                    
	                    possibleCorrections.setCount(posting, ((double) intersect.size())/union.size());
	                }
		        }
		    }
		}
		
//	    System.out.println("Word: " + word);
//	    System.out.println("KGram corrections----------------------");
//	    for (String entry : possibleCorrections.topK(20)) {
//	        double occurrences = index.get(getKgrams(entry,2).get(0)).getCount(entry);
//	        System.out.println(entry + " " + possibleCorrections.getCount(entry) + ": " + occurrences);
//	    }
		
		return possibleCorrections.topK(5);
	}
	
	
	private Set<String> getKgramsSet(String word, int k) {
	    Set<String> kgrams = new HashSet<String>();
	    for (int i = 0; i < k-1; i++) word = "$" + word + "$";
        for (int i = 0; i < word.length()-(k-1); i++) {
            String kgram = word.substring(i, i+k);
            kgrams.add(kgram);
        }
        return kgrams;
	}
	
	private ArrayList<String> getKgrams(String word, int k) {
	    ArrayList<String> kgrams = new ArrayList<String>();
	    for (int i = 0; i < k-1; i++) word = "$" + word + "$";
        for (int i = 0; i < word.length()-(k-1); i++) {
            String kgram = word.substring(i, i+k);
            kgrams.add(kgram);
        }
        return kgrams;
	}
	
	public double getOccurrences(String word) {
        return(index.get(getKgrams(word,2).get(0)).getCount(word));
	}

}
