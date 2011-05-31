All you will need to do is run setup.sh once, then run runNaiveBayes.sh with the argument for the classifier.

You will want....

Setup:
./setup.sh /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828

Part 1:
./runNaiveBayes.sh "binomial" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828

Part 2 (set number of features by modifying NUM_FEATURES):
./runNaiveBayes.sh "binomial-chi2" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828

Part 3:
./runNaiveBayes.sh "multinomial" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828

Part 5:
./runNaiveBayes.sh "twcnb" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828


INSTRUCTIONS FOR EXTRA CREDIT

NOTE: We could not include all the libraries that we used because the submission folder was too big.  Please email me (jhlau@stanford.edu) for the full libraries.

For reference the following lines perform MNB, converting the dataset into ARFF format requires the following command. ARFF describes data in the form of a list of documents sharing similar attributes. The TextDirectoryLoader class performs that transformation using a directory structure with folder names as labels. This class is included in our files.

** GETTING THE ARFF FILE **
java -cp weka.jar weka.core.converters.TextDirectoryLoader -dir /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828 > newstext.arff

Since Weka cannot handle string attributes we use a StringToWordVector filter to convert the strings to feature vectors. This can also be done at the GUI. Open the newstext.arff and at the Preprocess step choose "Filter" and then "Unsupervised >> attribute >> StringToWordVector" and then hit "Apply".

** NAIVE BAYES **
java -Xmx1512M -cp weka.jar weka.classifiers.meta.FilteredClassifier -t newstext.arff -F "weka.filters.unsupervised.attribute.StringToWordVector -S" -W weka.classifiers.bayes.NaiveBayes

** MULTINOMIAL NAIVE BAYES **
java -Xmx1512M -cp weka.jar weka.classifiers.meta.FilteredClassifier -t newstext.arff -F "weka.filters.unsupervised.attribute.StringToWordVector -S" -W weka.classifiers.bayes.NaiveBayesMultinomial

** 3NN **
java -Xmx1512M -cp weka.jar weka.classifiers.meta.FilteredClassifier -c last -t newstext.arff -F "weka.filters.unsupervised.attribute.StringToWordVector -R first -W 1000 -prune-rate -1.0 -N 0 -stemmer weka.core.stemmers.NullStemmer -M 1" -W weka.classifiers.lazy.IBk -D -- -K 3 -W 0 -A 

** SMO **
java -cp weka.jar weka.classifiers.bayes.NaiveBayesMultinomial -t newstext.arff -T newstext.arff -F "weka.filters.unsupervised.attribute.StringToWordVector -S" -W weka.classifiers.functions.SMO -- -N 2

We recommend using the GUI for LibSVM, particularly since the dependencies for LibSVM need to be included and some parameters for its use can be a little complicated at the command line. Adding LibSVM involves 

cp libsvm.jar /Applications/Weka/weka-3-6-4.app/Contents/Resources/Java/
export CLASSPATH=$CLASSPATH:/Applications/Weka/weka-3-6-4.app/Contents/Resources/Java/

DMNB was also done in this fashion.

With Sally we perform a similar transfom using delimiters on space and punctuation. We create vectors using tf-idf without any normalization. For more details please see http://www.rioleo.org/using-libsvm-for-text-categorization-on-a-mac-os-x.php

sally --input_format dir --chunk_size 128 --ngram_len 1 --ngram_delim "%0a%0d%20%22.,:;?" --vect_embed tfidf --vect_norm none --input_format dir --output_format libsvm data data.libsvm

Finally, we perform training using LibLinear
train -v 5 -c 100 data.libsvm

Dependencies:
1) Weka
2) LibSVM java class
3) LibLinear java class
