import java.util.*;
import cs224n.util.Counter;

public class NaiveBayesClassifier {
  
  static HashMap<Integer, List<String>> classToDocs;
  static HashMap<String, MessageFeatures> docToTokens;
  
  public static void doBinomial(MessageIterator mi) {
		initialize();
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
  public static void initialize() {

		classToDocs = new HashMap<Integer, List<String>>();
		docToTokens = new HashMap<String, MessageFeatures>();
  	
  	MessageIterator iterator = null;
  	
  	try {
    	iterator = new MessageIterator("train.gz");
  	} catch (Exception e) {
  		e.printStackTrace();
		}
		
		int numMessagesRead = 0;
  	try {
  		System.out.print("Initializing database of docs/words.");
    	while (true) {
    		MessageFeatures message = iterator.getNextMessage();
    		addMessageToDatabase(message);
    		
    		numMessagesRead++;
    		if (numMessagesRead % 1000 == 0) System.out.print(".");
    	}	
    	
  	} catch (Exception e) {
  		System.out.println("\nDone initializing database of docs/words");
		}
		
	}

	public static void addMessageToDatabase(MessageFeatures message) {
		
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
	
	public static void printNumClasses() {
		for (int key : classToDocs.keySet()) {
			System.out.println(key + ": " + classToDocs.get(key).size());
		}
	}

  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
}
