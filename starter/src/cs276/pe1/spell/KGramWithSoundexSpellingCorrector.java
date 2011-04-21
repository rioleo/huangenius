package cs276.pe1.spell;

import java.io.File;
import java.util.List;

import cs276.util.IOUtils;
import cs276.util.StringUtils;
import java.util.*;

import cs276.util.Counter;

public class KGramWithSoundexSpellingCorrector implements SpellingCorrector {
	/** Initializes spelling corrector by indexing kgrams in words from a file */
	
	KGramWithEditDistanceSpellingCorrector KGram;
	KGramSpellingCorrector simpleKGram;
	
	public KGramWithSoundexSpellingCorrector() {
	    KGram = new KGramWithEditDistanceSpellingCorrector();
	    simpleKGram = new KGramSpellingCorrector();
	}

	public List<String> corrections(String word) {
	
	    List<String> guesses = KGram.corrections(word);
	    List<String> tiedGuesses = new ArrayList<String>();
	    List<String> newGuesses = new ArrayList<String>();
	    Counter<String> frequencies = new Counter<String>();
	    
	    if (guesses != null && guesses.size() > 0) {
	        double closestEditDistance = KGram.getEditDistance(word, guesses.get(0));
	        for (String guess : guesses) {
	            if (KGram.getEditDistance(word, guess) == closestEditDistance) tiedGuesses.add(guess);
            }
        }
        
	    if (tiedGuesses != null && tiedGuesses.size() > 0) {
	        for (String guess : tiedGuesses) {
	            if (Soundex.soundex(word).equals(Soundex.soundex(guess))) {
	                newGuesses.add(guess);
	                return newGuesses;
                } 
            }
        }
        // didn't find soundex match
        
	    if (guesses != null && guesses.size() > 0) {
	        double closestEditDistance = KGram.getEditDistance(word, guesses.get(0));
	        for (String guess : guesses) {
	            if (KGram.getEditDistance(word, guess) == closestEditDistance) {
	                frequencies.setCount(guess, simpleKGram.getOccurrences(guess));
                } else {
                    break;
                }
            }
        }
        
        return frequencies.topK(1);
	        
	    // Use guess frequency as tie-break between guesses with same edit distance;

	    
	}

	
}
