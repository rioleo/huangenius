#./runNaiveBayes.sh "binomial" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828

#./runNaiveBayes.sh "binomial-chi2" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828

#./runNaiveBayes.sh "multinomial" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828

#./runNaiveBayes.sh "multinomial-chi2" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828

#./runNaiveBayes.sh "binomial-kfold" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828

#./runNaiveBayes.sh "multinomial-kfold" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828

#./runNaiveBayes.sh "binomial-kfold-chi2" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828

#./runNaiveBayes.sh "multinomial-kfold-chi2" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828

./runNaiveBayes.sh "twcnb" /afs/ir.stanford.edu/data/linguistic-data/TextCat/20Newsgroups/20news-18828

#Current accuracies:
#Binomial: 84.8%
#Binomial w/ Chi2: 87.0%
#100, 200, 400, 600, 800, 1600: 342, 341, 351, 344, 344, 340
#Multinomial: 96.5%
#Multinomial w/ Chi2: 91.8%
#100, 200, 400, 600, 800, 1600: 344, 359, 369, 368, 370, 378
#Binomial w/ Kfold=10: 77.6%
#Multinomial w/ Kfold=10: 89.7%
#Binomial w/ Kfold, Chi2: 83.1%
#Multinomial w/ Kfold, Chi2: 85.5%
#CNB: 384
#wCNB: 386
#CNB w/ Kfold=10: 89.6
#WCNB w/ Kfold=10: 89.6


#RANDOM KFOLDING:
#Binomial w/ Kfold=10: 
#Multinomial w/ Kfold=10: 89.5%
#Binomial w/ Kfold, Chi2: 
#Multinomial w/ Kfold, Chi2: 
#CNB: 384
#WCNB: 386
#CNB w/ Kfold=10: 89.7%
#WCNB w/ Kfold=10: 89.5%
