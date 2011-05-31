import java.util.*;
import cs224n.util.PriorityQueue;
import cs224n.util.*;

public class NaiveBayesClassifier {
  
  static HashMap<Integer, List<String>> classToDocs; // newsgroupnum -> list of docs
  static HashMap<String, MessageFeatures> docToTokens; // doc title -> tokens
  static Set<String> allTokens;
  static HashMap<String, HashMap<Integer, Double>> conditionalProbabilities; // tokens -> conditional probabilities(class -> score)
  
  static HashMap<Integer, Set<MessageFeatures>> kSets;
  
  static int numNewsgroups = 0;
  static int totalNumDocs = 0;
  
  static int K_FOLD_CONSTANT = 10;
  static int NUM_FEATURES = 300;
  static int[] FEATURES = {100, 200, 400, 600, 800, 1600};
  
  static int UPWEIGHT_SUBJECT_FACTOR = 1;
  
  static boolean TOP_FEATURES_BY_CLASS = true;
  
  
  public static void doBinomial(MessageIterator mi, boolean doChi2) {
    initialize(mi);

    trainBinomial();
		
		if (doChi2) {
      Set<String> topWords = getTopWordsByChiSquaredBinomial();
      testBinomial(topWords);
    } else {
      testBinomial(allTokens);
    }
  }
  
  public static void doBinomialKFold(MessageIterator mi, boolean doChi2) {
    initializeKSets(mi);
    System.out.println("K-fold: " + K_FOLD_CONSTANT);
    double totalAccuracy = 0;
    for (int i = 0; i < K_FOLD_CONSTANT; i++) {
      initializeKFold(i);
      trainBinomial();
      double accuracy = 0;
      if (doChi2) {
        Set<String> topWords = getTopWordsByChiSquaredBinomial();
        accuracy = testBinomialOnTestSet(topWords, kSets.get(i));
      } else {
        accuracy = testBinomialOnTestSet(allTokens, kSets.get(i));
      }
      totalAccuracy += accuracy;
      System.out.println(i + ": " + accuracy);
    }
    System.out.println("Done");
    System.out.println("Average accuracy: " + totalAccuracy/K_FOLD_CONSTANT);
  }
  
  public static void doMultinomial(MessageIterator mi, boolean doChi2) {
    initialize(mi);

    trainMultinomial();
		
		if (doChi2) {
      Set<String> topWords = getTopWordsByChiSquaredMultinomial();
      testMultinomial(topWords);
    } else {
      testMultinomial(allTokens);
    }
  }
  
  public static void doMultinomialKFold(MessageIterator mi, boolean doChi2) {
    initializeKSets(mi);
    System.out.println("K-fold: " + K_FOLD_CONSTANT);
    double totalAccuracy = 0;
    for (int i = 0; i < K_FOLD_CONSTANT; i++) {
      initializeKFold(i);
      trainMultinomial();
      double accuracy = 0;
      if (doChi2) {
        Set<String> topWords = getTopWordsByChiSquaredMultinomial();
        accuracy = testMultinomialOnTestSet(topWords, kSets.get(i));
      } else {
        accuracy = testMultinomialOnTestSet(allTokens, kSets.get(i));
      }
      totalAccuracy += accuracy;
      System.out.println(i + ": " + accuracy);
    }
    System.out.println("Done");
    System.out.println("Average accuracy: " + totalAccuracy/K_FOLD_CONSTANT);
  }
  
  public static void doTWCNB(MessageIterator mi) {
  
    // UNCOMMENT FOR TESTING ON FIRST 20 DOCS
    initialize(mi);
    trainMultinomialTWCNB();
    testMultinomialTWCNB(allTokens);
      
    // UNCOMMENT TO TEST CROSS VALIDATION
//    initializeKSets(mi);
//    System.out.println("K-fold: " + K_FOLD_CONSTANT);
//    double totalAccuracy = 0;
//    for (int i = 0; i < K_FOLD_CONSTANT; i++) {
//      initializeKFold(i);
//      trainMultinomialTWCNB();
////      Set<String> topWords = getTopWordsByChiSquaredMultinomial();
//      double accuracy = 0;
//      accuracy = testMultinomialTWCNBOnTestSet(allTokens, kSets.get(i));
//      totalAccuracy += accuracy;
//      System.out.println(i + ": " + accuracy);
//    }
//    System.out.println("Done");
//    System.out.println("Average accuracy: " + totalAccuracy/K_FOLD_CONSTANT);
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
      doBinomial(mi, false);
    } else if (mode.equals("binomial-chi2")) {
      doBinomial(mi, true);
    } else if (mode.equals("multinomial")) {
      doMultinomial(mi, false);
    } else if (mode.equals("multinomial-chi2")) {
      doMultinomial(mi, true);
    } else if (mode.equals("twcnb")) {
      doTWCNB(mi);
    } else if (mode.equals("binomial-kfold")) {
      doBinomialKFold(mi, false);
    } else if (mode.equals("binomial-kfold-chi2")) {
      doBinomialKFold(mi, true);
    } else if (mode.equals("multinomial-kfold")) {
      doMultinomialKFold(mi, false);
    } else if (mode.equals("multinomial-kfold-chi2")) {
      doMultinomialKFold(mi, true);
    } else { 
      // Add other test cases that you want to run here.
      
      System.err.println("Unknown mode "+mode);
      System.exit(-1);
    }
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

  public static void initialize(MessageIterator mi){
		classToDocs = new HashMap<Integer, List<String>>();
		docToTokens = new HashMap<String, MessageFeatures>();
    conditionalProbabilities = new HashMap<String, HashMap<Integer, Double>>();
		allTokens = new HashSet<String>();
  
  	MessageIterator iterator = mi;
  	numNewsgroups = iterator.numNewsgroups;
  
	  int numMessagesRead = 0;
	  int classNum = -1;
	  int numDocsInClass = 0;
	  try {
//		  System.out.print("Reading training set");
    	while (true) {
    		MessageFeatures message = iterator.getNextMessage();
    		if (message.newsgroupNumber != classNum) {
    			classNum = message.newsgroupNumber;
    			numDocsInClass = 1;
			  } else {
				  numDocsInClass++;
			  }
    		addMessageToDatabase(message);
    		
    		numMessagesRead++;
//    		if (numMessagesRead % 1000 == 0) System.out.print(".");
    	}	
    	
	  } catch (Exception e) {
		  totalNumDocs = numMessagesRead;
//		  System.out.println("Done");
	  }
  }
  
  public static void initializeKSets(MessageIterator mi) {
  
    Random rand = new Random();
  
    kSets = new HashMap<Integer, Set<MessageFeatures>>();
    for (int i = 0; i < K_FOLD_CONSTANT; i++) {
      kSets.put(i, new HashSet<MessageFeatures>());
    }
  
  	MessageIterator iterator = mi;
  	numNewsgroups = iterator.numNewsgroups;
  
	  int numMessagesRead = 0;
	  int classNum = -1;
	  try {
		  System.out.print("Reading training set");
    	while (true) {
    		MessageFeatures message = iterator.getNextMessage();
    		// NONRANDOM
//    		kSets.get(numMessagesRead%K_FOLD_CONSTANT).add(message);

        // RANDOM
        kSets.get(rand.nextInt(K_FOLD_CONSTANT)).add(message);
    		
    		if (message.newsgroupNumber != classNum) {
    			classNum = message.newsgroupNumber;
			  }
    		
    		numMessagesRead++;
    		if (numMessagesRead % 1000 == 0) System.out.print(".");
    	}	
    	
	  } catch (Exception e) {
		  totalNumDocs = numMessagesRead;
		  System.out.println("Done");
	  }
  
  }
  
  
  // Takes all sets that are not testIndex and adds it to training set
  public static void initializeKFold(int testIndex) {
	  classToDocs = new HashMap<Integer, List<String>>();
	  docToTokens = new HashMap<String, MessageFeatures>();
    conditionalProbabilities = new HashMap<String, HashMap<Integer, Double>>();
	  allTokens = new HashSet<String>();
  
    for (int i = 0; i < K_FOLD_CONSTANT; i++) {
      if (i != testIndex) {
        for (MessageFeatures doc : kSets.get(i)) {
          addMessageToDatabase(doc);
        } 
      }
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


	// BINOMIAL ----------------------------------------


  public static void trainBinomial() {
    for (MessageFeatures doc : docToTokens.values()) {
  		addOccurrencesToBinomialConditionalProbabilities(doc);
		}
  
		calculateBinomialConditionalProbabilities();
	}
  

	public static void addOccurrencesToBinomialConditionalProbabilities(MessageFeatures message) {
	
		Set<String> tokens = new HashSet<String>();
		tokens.addAll(message.subject.keySet());
		tokens.addAll(message.body.keySet());
		
		for (String token : tokens) {
			if (!conditionalProbabilities.containsKey(token)) conditionalProbabilities.put(token, new HashMap<Integer, Double>());
			HashMap<Integer, Double> occurrences = conditionalProbabilities.get(token);
			if (!occurrences.containsKey(message.newsgroupNumber)) {
				occurrences.put(message.newsgroupNumber, 1.0);
			} else {
				double numOccurrences = occurrences.get(message.newsgroupNumber);
				conditionalProbabilities.get(token).put(message.newsgroupNumber, numOccurrences+1);
			}
		}
	
	}


	// Calculates conditional probabilities for all tokens
	public static void calculateBinomialConditionalProbabilities() {
//		System.out.println("Calculating conditional probabilities..");
		int counter = 0;
		for (String token : conditionalProbabilities.keySet()) {
			counter++;
//			if (counter %1000 == 0) System.out.println(counter + "/" + allTokens.size());
			HashMap<Integer, Double> occurrences = conditionalProbabilities.get(token);
			for (int i = 0; i < numNewsgroups; i++) {
				if (!occurrences.containsKey(i)) occurrences.put(i, 0.0);
				 // Calculates cond prob with smoothing
				occurrences.put(i, (occurrences.get(i)+1)/(classToDocs.get(i).size()+2));
			}
		}
//		System.out.println("Done calculating conditional probabilities");
	}
  
  public static void testBinomial(Set<String> features) {
//		System.out.println("Classifying documents...");
		int total = 0;
		int correct = 0;
	
    for (int curNewsgroup = 0; curNewsgroup < numNewsgroups; curNewsgroup++) {

			// Get initial scores
			double[] initialScores = getInitialScores(features);
    
    	for (int testDoc = 0; testDoc < 20; testDoc++) {

				// Get tokens in query document
				MessageFeatures doc = docToTokens.get(classToDocs.get(curNewsgroup).get(testDoc));
				Set<String> docTokens = getTokensFromDoc(doc);
				int trueClass = doc.newsgroupNumber;
				
				// Calculate conditional probabilities for the document for each class
				double[] scores = new double[numNewsgroups];

				for (int newsgroup = 0; newsgroup < numNewsgroups; newsgroup++) {
				
					// Add prior
					double totalScore = initialScores[newsgroup];
					totalScore += Math.log(((double)classToDocs.get(newsgroup).size())/totalNumDocs);
					
					// Add conditional probability for every token
					for (String token : docTokens) {
					  if (!features.contains(token)) continue;
				    if (conditionalProbabilities.containsKey(token) && conditionalProbabilities.get(token).containsKey(newsgroup)) {
						  double conditionalProbability = conditionalProbabilities.get(token).get(newsgroup);
					    totalScore -= Math.log(1.0-conditionalProbability);
					    totalScore += Math.log(conditionalProbability);
            }
					}	
					
					// Store total score for class
					scores[newsgroup] = totalScore;
				}
				
				// Print out scores for document
//				System.out.println("Document " + doc + " class " + curNewsgroup);
				
				int bestClass = getBestClass(scores);
				if (bestClass == trueClass) correct++;
				total++;
				
				outputProbability(scores);
//				System.out.println("Accuracy: " + correct + "/" + total);
			}
		}
//		System.out.println("Accuracy: " + correct + "/" + total);
  
  }
  
  public static double[] getInitialScores(Set<String> features) {
    double[] initialScores = new double[numNewsgroups];
    for (int newsgroup = 0; newsgroup < numNewsgroups; newsgroup++) {
      double initialScore = 0;
      for (String token : features) {
        double conditionalProbability = conditionalProbabilities.get(token).get(newsgroup);
        initialScore += Math.log(1.0-conditionalProbability);
      }
      initialScores[newsgroup] = initialScore;
    }
    
    return initialScores;
  }
  
  public static Set<String> getTokensFromDoc(MessageFeatures message) {
		Set<String> docTokens = new HashSet<String>();
		docTokens.addAll(message.subject.keySet());
		docTokens.addAll(message.body.keySet());
    return docTokens;
  }
  
  public static int getBestClass(double[] scores) {
	  int bestClass = 0;
	  double bestScore = -100000;
	  for (int i = 0; i < numNewsgroups; i++) {
		  if (scores[i] > bestScore) {
			  bestClass = i;
			  bestScore = scores[i];
		  }
	  }
	  return bestClass;
  }
  
  
  public static double testBinomialOnTestSet(Set<String> features, Set<MessageFeatures> testSet) {
//		System.out.println("Classifying documents...");
		int total = 0;
		int correct = 0;

		// Get initial scores
		double[] initialScores = getInitialScores(features);
  
  	for (MessageFeatures doc : testSet) {
			// Get tokens in query document
			Set<String> docTokens = getTokensFromDoc(doc);
			int trueClass = doc.newsgroupNumber;
			
			// Calculate conditional probabilities for the document for each class
			double[] scores = new double[numNewsgroups];
			for (int newsgroup = 0; newsgroup < numNewsgroups; newsgroup++) {
			
				// Add prior
				double totalScore = initialScores[newsgroup];
				totalScore += Math.log(((double)classToDocs.get(newsgroup).size())/totalNumDocs);
				
				// Add conditional probability for every token
				for (String token : docTokens) {
					  if (!features.contains(token)) continue;
				    if (conditionalProbabilities.containsKey(token) && conditionalProbabilities.get(token).containsKey(newsgroup)) {
		          double conditionalProbability = conditionalProbabilities.get(token).get(newsgroup);
				      totalScore -= Math.log(1.0-conditionalProbability);
				      totalScore += Math.log(conditionalProbability);
			      }
				}	
				
				// Store total score for class
				scores[newsgroup] = totalScore;
			}
			
			// Print out scores for document			
			int bestClass = getBestClass(scores);
			if (bestClass == trueClass) correct++;
			total++;
			
//			System.out.println("Accuracy: " + correct + "/" + total + " out of a total " + testSet.size());
		}
		return ((double)correct)/total;

	}
  
  
	// END BINOMIAL ----------------------------------------
  
  
  
  
  // CHI SQUARED ----------------------------------------
  
  public static Set<String> getTopWordsByChiSquaredBinomial() {
//    System.out.print("Calculating chi2 values");
    Set<String> topWords = new HashSet<String>();
    Map<Integer, Counter<String>> newsgroupToWordOccurrences = new HashMap<Integer, Counter<String>>();
    Counter<String> wordsToTotalOccurrences = new Counter<String>();
    
    // Calculate occurrences for all words
		for (int newsgroup = 0; newsgroup < numNewsgroups; newsgroup++) {
		  newsgroupToWordOccurrences.put(newsgroup, new Counter<String>());
		  for (String docName : classToDocs.get(newsgroup)) {
		    MessageFeatures doc = docToTokens.get(docName);
	      Set<String> docTokens = new HashSet<String>();
	      docTokens.addAll(doc.subject.keySet());
	      docTokens.addAll(doc.body.keySet());
	      for (String token : docTokens) {
	        newsgroupToWordOccurrences.get(newsgroup).incrementCount(token);
	        wordsToTotalOccurrences.incrementCount(token);
	      }
		  }
	  }
    
    Counter<String> topTokens = new Counter<String>();
    
    // Iterate through newsgroups
		for (int newsgroup = 0; newsgroup < numNewsgroups; newsgroup++) {
//      System.out.print(".");
      
		  // Get all tokens in newsgroup
		  Counter<String> newsgroupTokens = newsgroupToWordOccurrences.get(newsgroup);
		  
		  // Calculate and sort by chi-2 value
		  Counter<String> chi2s = new Counter<String>();
		  double N = (double)totalNumDocs;
		  for (String token : newsgroupTokens.keySet()) {
		    double A = newsgroupTokens.getCount(token);
		    double B = (double)classToDocs.get(newsgroup).size() - A;
		    double C = wordsToTotalOccurrences.getCount(token) - A;
		    double D = totalNumDocs - classToDocs.get(newsgroup).size() - C;
		    
        topTokens.incrementCount(token, (N*Math.pow(A*D-C*B,2)) / ((A+C)*(B+D)*(A+B)*(C+D)));
		    chi2s.setCount(token, (N*Math.pow(A*D-C*B,2)) / ((A+C)*(B+D)*(A+B)*(C+D)));
		  }
		  
		  // Retrieve top 300 for this class
		  if (TOP_FEATURES_BY_CLASS) {
		    PriorityQueue<String> sortedTokens = chi2s.asPriorityQueue();
		    for (int i = 0; i < NUM_FEATURES; i++) {
		      if (!sortedTokens.hasNext()) break;
  //		    if (newsgroup == 0)   System.out.println(sortedTokens.next());
		      topWords.add(sortedTokens.next());
		    }
	    }
		  
	  }
		
	  if (!TOP_FEATURES_BY_CLASS) {
		  PriorityQueue<String> sortedTokens = topTokens.asPriorityQueue();
		  for (int i = 0; i < NUM_FEATURES; i++) {
		    if (!sortedTokens.hasNext()) break;
		    topWords.add(sortedTokens.next());
		  }
	  }
		
//    System.out.println("Done");
//    System.out.println(topWords.size() + " words chosen");
    return topWords;
  }
  

  public static Set<String> getTopWordsByChiSquaredMultinomial() {
//    System.out.print("Calculating chi2 values");
    Set<String> topWords = new HashSet<String>();
    Map<Integer, Counter<String>> newsgroupToWordOccurrences = new HashMap<Integer, Counter<String>>();
    Counter<String> wordsToTotalOccurrences = new Counter<String>();
    Map<Integer, Integer> newsgroupToTotalWords = new HashMap<Integer, Integer>();
    
    // Calculate occurrences for all words
		for (int newsgroup = 0; newsgroup < numNewsgroups; newsgroup++) {
		  newsgroupToTotalWords.put(newsgroup, 0);
		  newsgroupToWordOccurrences.put(newsgroup, new Counter<String>());
		  for (String docName : classToDocs.get(newsgroup)) {
		    MessageFeatures doc = docToTokens.get(docName);
	      for (String token : doc.subject.keySet()) {
	        double occurrences = doc.subject.getCount(token);
	        newsgroupToWordOccurrences.get(newsgroup).incrementCount(token, occurrences);
	        wordsToTotalOccurrences.incrementCount(token, occurrences);
	        newsgroupToTotalWords.put(newsgroup, newsgroupToTotalWords.get(newsgroup) + (int)occurrences);
	      }
	      for (String token : doc.body.keySet()) {
	        double occurrences = doc.body.getCount(token);
	        newsgroupToWordOccurrences.get(newsgroup).incrementCount(token, occurrences);
	        wordsToTotalOccurrences.incrementCount(token, occurrences);
	        newsgroupToTotalWords.put(newsgroup, newsgroupToTotalWords.get(newsgroup) + (int)occurrences);
	      }
		  }
	  }
	  
	  // Calculate N
	  int totalNumTokens = 0;
	  for (int newsgroup = 0; newsgroup < numNewsgroups; newsgroup++) {
	    totalNumTokens += newsgroupToTotalWords.get(newsgroup);
	  }
    
    Counter<String> topTokens = new Counter<String>();
    
    // Iterate through newsgroups
		for (int newsgroup = 0; newsgroup < numNewsgroups; newsgroup++) {
//      System.out.print(".");
      
		  // Get all tokens in newsgroup
		  Counter<String> newsgroupTokens = newsgroupToWordOccurrences.get(newsgroup);
		  
		  // Calculate and sort by chi-2 value
		  Counter<String> chi2s = new Counter<String>();
		  double N = (double)totalNumTokens;
		  for (String token : newsgroupTokens.keySet()) {
		    double A = newsgroupTokens.getCount(token);
		    double B = (double)newsgroupToTotalWords.get(newsgroup) - A;
		    double C = wordsToTotalOccurrences.getCount(token) - A;
		    double D = N - A - B - C;

		    chi2s.setCount(token, (N*Math.pow(A*D-C*B,2)) / ((A+C)*(B+D)*(A+B)*(C+D)));
        topTokens.incrementCount(token, (N*Math.pow(A*D-C*B,2)) / ((A+C)*(B+D)*(A+B)*(C+D)));
		  }
		  
		  // Retrieve top 300 for this class
		  if (TOP_FEATURES_BY_CLASS) {
		    PriorityQueue<String> sortedTokens = chi2s.asPriorityQueue();
		    for (int i = 0; i < NUM_FEATURES; i++) {
		      if (!sortedTokens.hasNext()) break;
  //		    if (newsgroup == 0)   System.out.println(sortedTokens.next());
		      topWords.add(sortedTokens.next());
		    }
	    }
		  
	  }
	  
	  if (!TOP_FEATURES_BY_CLASS) {
		  PriorityQueue<String> sortedTokens = topTokens.asPriorityQueue();
		  for (int i = 0; i < NUM_FEATURES; i++) {
		    if (!sortedTokens.hasNext()) break;
		    topWords.add(sortedTokens.next());
		  }
	  }
		
//    System.out.println("Done");
//    System.out.println(topWords.size() + " words chosen");
    return topWords;
  }
  
//  End CHI SQUARED ---------------------------------

   
  
//  MULTINOMIAL --------------------------------------- 

  public static void trainMultinomial() {
    for (MessageFeatures doc : docToTokens.values()) {
  		addOccurrencesToMultinomialConditionalProbabilities(doc);
		}
  
		calculateMultinomialConditionalProbabilities();
  }

  public static Counter<String> getTotalCounter(MessageFeatures message) {
	  Counter<String> tokens = new Counter<String>();
	  tokens.incrementAll(message.subject);
	  tokens.incrementAll(message.body); 
	  return tokens;
  }


	public static void addOccurrencesToMultinomialConditionalProbabilities(MessageFeatures message) {
	  
	  Counter<String> tokens = getTotalCounter(message);
	  new Counter<String>();
	  for (int i = 0; i < UPWEIGHT_SUBJECT_FACTOR; i++)
	  tokens.incrementAll(message.subject);
	  tokens.incrementAll(message.body);
		
		for (String token : tokens.keySet()) {
			if (!conditionalProbabilities.containsKey(token)) conditionalProbabilities.put(token, new HashMap<Integer, Double>());
			HashMap<Integer, Double> occurrences = conditionalProbabilities.get(token);
			if (!occurrences.containsKey(message.newsgroupNumber)) {
				occurrences.put(message.newsgroupNumber, tokens.getCount(token));
			} else {
				double numOccurrences = occurrences.get(message.newsgroupNumber);
				conditionalProbabilities.get(token).put(message.newsgroupNumber, numOccurrences+tokens.getCount(token));
			}
		}
	
	}

	// Calculates conditional probabilities for all tokens
	public static void calculateMultinomialConditionalProbabilities() {
//		System.out.println("Calculating conditional probabilities..");
		int counter = 0;
		
		HashMap<Integer, Integer> totalTokensInClass = getTotalTokensByClass();
		
		for (String token : conditionalProbabilities.keySet()) {
			counter++;
//			if (counter %1000 == 0) System.out.println(counter + "/" + allTokens.size());
			HashMap<Integer, Double> occurrences = conditionalProbabilities.get(token);
			for (int i = 0; i < numNewsgroups; i++) {
				if (!occurrences.containsKey(i)) occurrences.put(i, 0.0);
				 // Calculates cond prob with smoothing
				occurrences.put(i, (occurrences.get(i)+1)/(totalTokensInClass.get(i) + conditionalProbabilities.keySet().size()));
			}
		}
//		System.out.println("Done calculating conditional probabilities");
	}

  public static HashMap<Integer, Integer> getTotalTokensByClass() {
		HashMap<Integer, Integer> numTokensInClass = new HashMap<Integer, Integer>();
		for (int i = 0; i < numNewsgroups; i++) {
		  numTokensInClass.put(i, 0);
		}
		for (String token : conditionalProbabilities.keySet()) {
		  HashMap<Integer, Double> tokenProbabilities = conditionalProbabilities.get(token);
		  for (Integer newsgroup : tokenProbabilities.keySet()) {
		    numTokensInClass.put(newsgroup, numTokensInClass.get(newsgroup) + tokenProbabilities.get(newsgroup).intValue());
		  }
		}
    return numTokensInClass;
  }


  public static void testMultinomial(Set<String> features) {
//		System.out.println("Classifying documents...");
		int total = 0;
		int correct = 0;
	
    for (int curNewsgroup = 0; curNewsgroup < numNewsgroups; curNewsgroup++) {
    	for (int testDoc = 0; testDoc < 20; testDoc++) {

				// Get tokens in query document
				MessageFeatures doc = docToTokens.get(classToDocs.get(curNewsgroup).get(testDoc));
				Counter<String> docTokens = getTotalCounter(doc);
				int trueClass = doc.newsgroupNumber;
				
				// Calculate conditional probabilities for the document for each class
				double[] scores = new double[numNewsgroups];

				for (int newsgroup = 0; newsgroup < numNewsgroups; newsgroup++) {
				
					// Add prior
					double totalScore = 0;
					totalScore += Math.log(((double)classToDocs.get(newsgroup).size())/totalNumDocs);
					
					// Add conditional probability for every token
					for (String token : docTokens.keySet()) {
					  if (!features.contains(token)) continue;
				    if (conditionalProbabilities.containsKey(token) && conditionalProbabilities.get(token).containsKey(newsgroup)) {
						  double conditionalProbability = conditionalProbabilities.get(token).get(newsgroup);
					    totalScore += (Math.log(conditionalProbability)*docTokens.getCount(token));
            }
					}	
					
					// Store total score for class
					scores[newsgroup] = totalScore;
				}
				
				// Print out scores for document
//				System.out.println("Document " + doc + " class " + curNewsgroup);
				
				int bestClass = getBestClass(scores);
				if (bestClass == trueClass) correct++;
				total++;
				
				outputProbability(scores);
//				System.out.println("Accuracy: " + correct + "/" + total);
			}
		}
//		System.out.println("Accuracy: " + correct + "/" + total);
  
  }

  public static double testMultinomialOnTestSet(Set<String> features, Set<MessageFeatures> testSet) {
		System.out.println("Classifying documents...");
		int total = 0;
		int correct = 0;
	
    for (MessageFeatures doc : testSet) {
		  // Get tokens in query document
		  Counter<String> docTokens = getTotalCounter(doc);
		  int trueClass = doc.newsgroupNumber;
		
		  // Calculate conditional probabilities for the document for each class
		  double[] scores = new double[numNewsgroups];
		  for (int newsgroup = 0; newsgroup < numNewsgroups; newsgroup++) {
		
			  // Add prior
			  double totalScore = 0;
			  totalScore += Math.log(((double)classToDocs.get(newsgroup).size())/totalNumDocs);
			
			  // Add conditional probability for every token
			  for (String token : docTokens.keySet()) {
			    if (!features.contains(token)) continue;
		      if (conditionalProbabilities.containsKey(token) && conditionalProbabilities.get(token).containsKey(newsgroup)) {
				    double conditionalProbability = conditionalProbabilities.get(token).get(newsgroup);
			      totalScore += (Math.log(conditionalProbability)*docTokens.getCount(token));
          }
			  }	
			
			  // Store total score for class
			  scores[newsgroup] = totalScore;
		  }
		
		  int bestClass = getBestClass(scores);
		  if (bestClass == trueClass) correct++;
		  total++;
		}
		return ((double)correct)/total;
  
  }



//  END MULTINOMIAL -------------------------------------
  
  
  
  
//  TWCNB ------------------------------------------------

  public static void trainMultinomialTWCNB() {
	  // Get binomial occurrences for transform 2
	  Counter<String> binomialOccurrences = getOccurrences();
	  
    for (MessageFeatures doc : docToTokens.values()) {
  		addOccurrencesToMultinomialTWCNBConditionalProbabilities(doc, binomialOccurrences);
		}
  
		calculateCNBConditionalProbabilities();
  }
  
  // Transforms happen here
 	public static void addOccurrencesToMultinomialTWCNBConditionalProbabilities(MessageFeatures message, Counter<String> binomialOccurrences) {
	  
	  Counter<String> tokens = getTotalCounter(message);
	  new Counter<String>();
	  tokens.incrementAll(message.subject);
	  tokens.incrementAll(message.body);
	  
	  double lengthNormalization = 0;
	  for (String token : tokens.keySet()) lengthNormalization += Math.pow(tokens.getCount(token),2);
		
		for (String token : tokens.keySet()) {
			if (!conditionalProbabilities.containsKey(token)) conditionalProbabilities.put(token, new HashMap<Integer, Double>());
			HashMap<Integer, Double> occurrences = conditionalProbabilities.get(token);
			double f = tokens.getCount(token);
			f = Math.log(1+f); // transform 1
      f = f*Math.log(docToTokens.size()/((double)binomialOccurrences.getCount(token))); // transform 2
      f = f/Math.sqrt(lengthNormalization); // transform 3
			if (!occurrences.containsKey(message.newsgroupNumber)) {
				occurrences.put(message.newsgroupNumber, f);
			} else {
				double numOccurrences = occurrences.get(message.newsgroupNumber);
				conditionalProbabilities.get(token).put(message.newsgroupNumber, numOccurrences+f);
			}
		}
	
	}
  

	// Calculates conditional probabilities for all tokens
	public static void calculateCNBConditionalProbabilities() {
//		System.out.println("Calculating conditional probabilities..");
		int counter = 0;
		
		HashMap<Integer, Integer> totalTokensInClass = getTotalTokensByClass();
		
		for (String token : conditionalProbabilities.keySet()) {
			counter++;
			HashMap<Integer, Double> occurrences = conditionalProbabilities.get(token);
			
			double totalOccurrences = getTotalOccurrences(totalTokensInClass);
			double totalTokenOccurrences = getTotalTokenOccurrences(conditionalProbabilities, token);
			
			for (int i = 0; i < numNewsgroups; i++) {
				if (!occurrences.containsKey(i)) occurrences.put(i, 0.0);
			  // Calculates COMPLEMENT cond prob with smoothing
			  double numerator = totalTokenOccurrences - occurrences.get(i) + 1;
			  double denominator = totalOccurrences - totalTokensInClass.get(i) + conditionalProbabilities.keySet().size();
        occurrences.put(i, (numerator)/(denominator));				 
			}
		}
//		System.out.println("Done calculating conditional probabilities");
	}


  public static Counter<String> getOccurrences() {
    Counter<String> occurrences = new Counter<String>();
    for (MessageFeatures doc : docToTokens.values()) {
      Set<String> docTokens = new HashSet<String>();
      docTokens.addAll(doc.subject.keySet());
      docTokens.addAll(doc.body.keySet());
      for (String token : docTokens) {
        occurrences.incrementCount(token);
      }
    }
    return occurrences;
  }


  public static double getTotalOccurrences(HashMap<Integer, Integer> occurrences) {
    int total = 0;
    for (Integer num : occurrences.values()) total+= num;
    return total;  
  }
  
  public static double getTotalTokenOccurrences(HashMap<String, HashMap<Integer, Double>> probs, String token) {
    int total = 0;
    for (Double num : probs.get(token).values()) total+= num.intValue();
    return total;
  }


  public static void testMultinomialTWCNB(Set<String> features) {
//		System.out.println("Classifying documents...");
		int total = 0;
		int correct = 0;
	
	  //  UNCOMMENT FOR WCNB Get normalization factors
	  double[] normalizationFactors = getNormalizationFactors();
	  double priorNormalizationFactor = getPriorNormalizationFactor();
	
    for (int curNewsgroup = 0; curNewsgroup < numNewsgroups; curNewsgroup++) {
      int correctInNewsgroup = 0;
    	for (int testDoc = 0; testDoc < 20; testDoc++) {

				// Get tokens in query document
				MessageFeatures doc = docToTokens.get(classToDocs.get(curNewsgroup).get(testDoc));
				Counter<String> docTokens = getTotalCounter(doc);
				int trueClass = doc.newsgroupNumber;
				
				// Calculate conditional probabilities for the document for each class
				double[] scores = new double[numNewsgroups];

				for (int newsgroup = 0; newsgroup < numNewsgroups; newsgroup++) {
				
					// Add prior
					double totalScore = 0;
					
					// WCNB
//					totalScore += (Math.log(((double)classToDocs.get(newsgroup).size())/totalNumDocs)) / priorNormalizationFactor;
					// CNB
//					totalScore += Math.log(((double)classToDocs.get(newsgroup).size())/totalNumDocs);
					
					// Add conditional probability for every token
					for (String token : docTokens.keySet()) {
					  if (!features.contains(token)) continue;
				    if (conditionalProbabilities.containsKey(token) && conditionalProbabilities.get(token).containsKey(newsgroup)) {
						  double conditionalProbability = conditionalProbabilities.get(token).get(newsgroup);
						  
						  // UNCOMMENT FOR WCNB (uncomment above as well)
						  double numerator = Math.log(conditionalProbability);
						  double denominator = normalizationFactors[newsgroup];
						  totalScore += numerator/denominator;
						  
						  // UNCOMMENT FOR CNB
//					    totalScore -= (Math.log(conditionalProbability)*docTokens.getCount(token));
            }
					}	
					
					// Store total score for class
					scores[newsgroup] = totalScore;
				}
				
				// Print out scores for document
//				System.out.println("Document " + doc + " class " + curNewsgroup);
				
		  // UNCOMMENT FOR CNB
//		  int bestClass = getBestClass(scores);
		  // UNCOMMENT FOR WCNB
		    int bestClass = getBestClassMin(scores);
				if (bestClass == trueClass) correct++;
				total++;
				if (bestClass == trueClass) correctInNewsgroup++;
				
				outputProbability(scores);
//				System.out.println("Accuracy: " + correct + "/" + total);
			}
//			System.out.println("Accuracy for newsgroup " + curNewsgroup + ": " + correctInNewsgroup + "/20");
		}
//		System.out.println("Accuracy: " + correct + "/" + total);
  
  }

  public static double[] getNormalizationFactors() {
    double[] normalizationFactors = new double[numNewsgroups];
    for (int i = 0; i < numNewsgroups; i++) {
        normalizationFactors[i] = 0;
    }
    for (String token : conditionalProbabilities.keySet()) {
      HashMap<Integer, Double> probs = conditionalProbabilities.get(token);
      for (int i = 0; i < numNewsgroups; i++) {
        normalizationFactors[i] += Math.abs(Math.log(probs.get(i)));
      }
    }
    return normalizationFactors;  
  }
  
  public static double getPriorNormalizationFactor() {
    double total = 0;
    for (int newsgroup = 0; newsgroup < numNewsgroups; newsgroup++)	total += Math.abs(Math.log(((double)classToDocs.get(newsgroup).size())/totalNumDocs));
    return total;
  }


  public static double testMultinomialTWCNBOnTestSet(Set<String> features, Set<MessageFeatures> testSet) {
		System.out.println("Classifying documents...");
		int total = 0;
		int correct = 0;
	
	  //  UNCOMMENT FOR WCNB Get normalization factors
	  double[] normalizationFactors = getNormalizationFactors();
	  double priorNormalizationFactor = getPriorNormalizationFactor();
	
    for (MessageFeatures doc : testSet) {
		  // Get tokens in query document
		  Counter<String> docTokens = getTotalCounter(doc);
		  int trueClass = doc.newsgroupNumber;
		
		  // Calculate conditional probabilities for the document for each class
		  double[] scores = new double[numNewsgroups];
		  for (int newsgroup = 0; newsgroup < numNewsgroups; newsgroup++) {
		
			  // Add prior
			  double totalScore = 0;
			  // UNCOMMENT FOR CNB
//			  totalScore += Math.log(((double)classToDocs.get(newsgroup).size())/totalNumDocs);
			
			  // Add conditional probability for every token
			  for (String token : docTokens.keySet()) {
			    if (!features.contains(token)) continue;
		      if (conditionalProbabilities.containsKey(token) && conditionalProbabilities.get(token).containsKey(newsgroup)) {
				    double conditionalProbability = conditionalProbabilities.get(token).get(newsgroup);
				    
					  // UNCOMMENT FOR WCNB (uncomment above as well)
				    double numerator = Math.log(conditionalProbability);
				    double denominator = normalizationFactors[newsgroup];
				    totalScore += numerator/denominator;
				    
				    // UNCOMMENT FOR CNB
//			      totalScore -= (Math.log(conditionalProbability)*docTokens.getCount(token));
          }
			  }	
			
			  // Store total score for class
			  scores[newsgroup] = totalScore;
		  }
		
		  // UNCOMMENT FOR CNB
//		  int bestClass = getBestClass(scores);
		  // UNCOMMENT FOR WCNB
		  int bestClass = getBestClassMin(scores);
		  if (bestClass == trueClass) correct++;
		  total++;
		}
		return ((double)correct)/total;
  }

  public static int getBestClassMin(double[] scores) {
	  int bestClass = 0;
	  double bestScore = 100000;
	  for (int i = 0; i < numNewsgroups; i++) {
		  if (scores[i] < bestScore) {
			  bestClass = i;
			  bestScore = scores[i];
		  }
	  }
	  return bestClass;
  }

//  END TWCNB --------------------------------------------













  
}
