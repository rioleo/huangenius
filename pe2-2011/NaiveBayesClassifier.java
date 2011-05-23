import java.util.*;
import cs224n.util.Counter;

public class NaiveBayesClassifier {
  
  static HashMap<String, List<String>> classToDocs;
  static HashMap<String, Counter<String>> docToTokens;
  
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
  	try {
  		
			classToDocs = new HashMap<String, List<String>>();
			docToTokens = new HashMap<String, Counter<String>>();
  	
  	
    	MessageIterator iter = new MessageIterator("train.gz");
    	MessageFeatures message = iter.getNextMessage();
    	System.out.println(message.fileName);
    	System.out.println(message.newsgroupNumber);
    	
    	
    	for (String token : message.subject.keySet()) {
//    		System.out.println(token + " " + message.subject.getCount(token));
  		}
  		
    	for (String token : message.body.keySet()) {
//    		System.out.println(token + " " + message.body.getCount(token));
  		}
    	
  	} catch (Exception e) {
  		e.printStackTrace();
		}
  }
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
}
