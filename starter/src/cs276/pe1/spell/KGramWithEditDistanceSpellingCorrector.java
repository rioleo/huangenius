package cs276.pe1.spell;

import java.io.File;
import java.util.List;

import cs276.util.IOUtils;
import cs276.util.StringUtils;
import java.util.*;

import cs276.util.Counter;

public class KGramWithEditDistanceSpellingCorrector implements SpellingCorrector {
	/** Initializes spelling corrector by indexing kgrams in words from a file */
	
	KGramSpellingCorrector KGram;
	
	public KGramWithEditDistanceSpellingCorrector() {
	    KGram = new KGramSpellingCorrector();
	}

	public List<String> corrections(String word) {
	    List<String> guesses = KGram.corrections(word);
	    
	    // Analyze edit distances
	    Counter<String> editDistances = new Counter<String>();
	    for (String guess : guesses) {
	        editDistances.setCount(guess, -1*StringUtils.levenshtein(word, guess));
        }	    
	    
//	    System.out.println("Using edit distances--------------------");
//	    for (String guess : editDistances.topK(5)) {
//	        System.out.println(guess + ": " + editDistances.getCount(guess));
//	    }
	    
		return editDistances.topK(5);
	}
	
	public double getEditDistance(String word, String guess) {
	    return(-1*StringUtils.levenshtein(word, guess));
	}

	
}
