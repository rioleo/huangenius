import java.util.*;
import cs224n.util.Counter;

public class NaiveBayesClassifier {
  
  static HashMap<Integer, List<String>> classToDocs; // newsgroupnum -> list of docs
  static HashMap<String, MessageFeatures> docToTokens; // doc title -> tokens
  static Set<String> allTokens;
  static HashMap<String, HashMap<Integer, Double>> conditionalProbabilities; // tokens -> conditional probabilities(class -> score)
  
  static int numNewsgroups = 0;
  static int totalNumDocs = 0;
  
  public static void doBinomial(MessageIterator mi) {
		initialize(mi);
		
		calculateBinomialConditionalProbabilities();
		
  	MessageIterator iterator = getIterator();
		
    for (int curNewsgroup = 0; curNewsgroup < numNewsgroups; curNewsgroup++) {
    	for (int doc = 0; doc < 20; doc++) {

				// Get tokens in query document
				MessageFeatures message = docToTokens.get(classToDocs.get(curNewsgroup).get(doc));
				Set<String> docTokens = new HashSet<String>();
				docTokens.addAll(message.subject.keySet());
				docTokens.addAll(message.body.keySet());
				
				// Calculate conditional probabilities for the document for each class
				double[] scores = new double[numNewsgroups];
				for (int newsgroup = 0; newsgroup < numNewsgroups; newsgroup++) {
				
					// Add prior
					double totalScore = 0;
					totalScore += Math.log(((double)classToDocs.get(newsgroup).size())/totalNumDocs);
					
					// Add conditional probability for every token
					for (String token : allTokens) {
						double conditionalProbability = conditionalProbabilities.get(token).get(newsgroup);
						if (docTokens.contains(token))	totalScore += Math.log(conditionalProbability);
						else totalScore += Math.log(1-conditionalProbability);
					}	
					
					// Store total score for class
					scores[newsgroup] = totalScore;
				}
				
				// Print out scores for document
				System.out.println("Document " + doc + " class " + curNewsgroup);
				outputProbability(scores);
			
			}
		
		}

  }
  
  public static void doBinomialChi2(MessageIterator mi) {
    // Your code here.
  }
  
  public static void doMultinomial(MessageIterator mi) {
    // Your code here.
  }
  
  public static void doTWCNB(MessageIterator mi) {
    // Your code here.
  }
  
  public static void outputProbability( final double[] probability )
  {
	  for ( int i = 0; i < probability.length; i ++ )
	  {
		  if ( i == 0 )
			  System.out.format( "%1.8g", probability[i] );
		  else
			  System.out.format( "\t%1.8g", probability[i] );
	  }
	  System.out.format( "%n" );
 }
  
  // Starter code
  public static void main(String args[]) {
    if (args.length != 2) {
      System.err.println("Usage: NaiveBayesClassifier <mode> <train>");
      System.exit(-1);
    }
    String mode = args[0];
    String train = args[1];
    
    MessageIterator mi = null;
    try {
      mi = new MessageIterator(train);
      
    } catch (Exception e) {
      System.err.println("Unable to create message iterator from file "+train);
      e.printStackTrace();
      System.exit(-1);
    }
    
    if (mode.equals("binomial")) {
      doBinomial(mi);
    } else if (mode.equals("binomial-chi2")) {
      doBinomialChi2(mi);
    } else if (mode.equals("multinomial")) {
      doMultinomial(mi);
    } else if (mode.equals("twcnb")) {
      doTWCNB(mi);
    } else { 
      // Add other test cases that you want to run here.
      
      System.err.println("Unknown mode "+mode);
      System.exit(-1);
    }
  }
  
  
  // Written by Joe
  public static void initialize(MessageIterator mi) {

		classToDocs = new HashMap<Integer, List<String>>();
		docToTokens = new HashMap<String, MessageFeatures>();
		allTokens = new HashSet<String>();
  	
  	MessageIterator iterator = mi;
  	numNewsgroups = iterator.numNewsgroups;
  	
/*  	try {
    	iterator = new MessageIterator("train.gz");
    	numNewsgroups = iterator.numNewsgroups;
  	} catch (Exception e) {
  		e.printStackTrace();
		}*/
		
		int numMessagesRead = 0;
  	try {
  		System.out.print("Initializing database of docs/words");
    	while (true) {
    		MessageFeatures message = iterator.getNextMessage();
    		addMessageToDatabase(message);
    		
    		numMessagesRead++;
    		if (numMessagesRead % 1000 == 0) System.out.print(".");
    	}	
    	
  	} catch (Exception e) {
  		totalNumDocs = numMessagesRead;
  		System.out.println("\nDone initializing database of docs/words");
		}
		
	}

	public static void addMessageToDatabase(MessageFeatures message) {
		
		allTokens.addAll(message.subject.keySet());
		allTokens.addAll(message.body.keySet());
		
		int newsgroupNumber = message.newsgroupNumber;
		String filename = message.fileName;

		// Add document to class->docs hashmap		
		if (!classToDocs.containsKey(newsgroupNumber)) {
			classToDocs.put(newsgroupNumber, new ArrayList<String>());
		}
		classToDocs.get(newsgroupNumber).add(filename);
		
		// Add MessageFeatures to doc->wordcounter hashmap
		docToTokens.put(filename, message);
  	
	}
	
	public static MessageIterator getIterator() {
  	try {
    	MessageIterator iterator = new MessageIterator("train.gz");
    	return iterator;
  	} catch (Exception e) {
  		e.printStackTrace();
		}
		return null;
	}
	
	public static void printNumClasses() {
		for (int key : classToDocs.keySet()) {
			System.out.println(key + ": " + classToDocs.get(key).size());
		}
	}


	// BINOMIAL ----------------------------------------


  // Calculates conditional probabilties for ALL tokens... don't use this.
  public static void calculateBinomialConditionalProbabilities() {
  	System.out.println("Calculating conditional probabilities for Binomial:");
  	conditionalProbabilities = new HashMap<String, HashMap<Integer, Double>>();
		int numTokensCalculated = 0;
		int numTokensTotal = allTokens.size();

  	// Calculate conditional probabilities for all tokens
  	for (String token : allTokens) {
  		conditionalProbabilities.put(token, new HashMap<Integer, Double>());
  		numTokensCalculated++;
  		if (numTokensCalculated % 1000 == 0) System.out.println(numTokensCalculated + "/" + allTokens.size());
  			
			// Calculate the token's probability for each class
			for (int newsgroup = 0; newsgroup < numNewsgroups; newsgroup++) {
			
				// Iterate through all docs in the class
				int occurrence = 0;
				int total = 0;
				for (String doc : classToDocs.get(newsgroup)) {
					MessageFeatures messageData = docToTokens.get(doc);
					if (messageData.subject.containsKey(token)) occurrence++;
					total++;
				}
				
				// Store conditional probability WITH SMOOTHING
				conditionalProbabilities.get(token).put(newsgroup, ((double)occurrence+1)/(total+2));
			}
  	
  	}
  
		System.out.println("Done calculating conditional probabilities for Binomial");
  }
  
  // Gets conditional probability for one token in one newsgroup
  public static double getBinomialConditionalProbability(String token, int newsgroup) {
  
		// Iterate through all docs in the class
		int occurrence = 0;
		int total = 0;
		for (String doc : classToDocs.get(newsgroup)) {
			MessageFeatures messageData = docToTokens.get(doc);
			if (messageData.subject.containsKey(token)) occurrence++;
			total++;
		}
		
		// Return conditional probability WITH SMOOTHING
		return ((double)occurrence + 1)/(total + 2);
  
  }
  
  
  
  
	// END BINOMIAL ----------------------------------------
  
  
  
  
  // CHI SQUARED ----------------------------------------
  
  
  
  
  
  
  
  
}
