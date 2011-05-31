All you will need to do is run setup.sh once, then run runNaiveBayes.sh with the argument for the classifier.

You will want....

Part 1:
./runNaiveBayes.sh "binomial" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828

Part 2 (set number of features by modifying NUM_FEATURES):
./runNaiveBayes.sh "binomial-chi2" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828

Part 3:
./runNaiveBayes.sh "multinomial" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828

Part 5:
./runNaiveBayes.sh "twcnb" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828
