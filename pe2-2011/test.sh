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
#K=100, 200, 400, 600, 800, 1600: 342, 341, 351, 344, 344, 340
#Multinomial: 96.5%
#Multinomial w/ Chi2: 91.8%
#K=100, 200, 400, 600, 800, 1600: 344, 359, 369, 368, 370, 378
#CNB: 384
#wCNB: 386



#RANDOM KFOLDING:
#Binomial w/ Kfold=10: 
#Multinomial w/ Kfold=10: 89.5%-89.65%
#Binomial w/ Kfold, Chi2: 
#Multinomial w/ Kfold, Chi2: 
#CNB: 384
#WCNB: 386
#WCNB+T1: 385
#WCNB+T1+T2: 389
#WCNB+T1+T2+T3: 382
#CNB w/ Kfold=10: 89.7%
#WCNB w/ Kfold=10: 89.5%
#WCNB +1,2,3 w/ Kfold=10: 89.2%



#Other stuff
#MNB w/ upweight 5x: 90.0
#MNB w/ upweight 10x: 90.3
#MNB w/ upweight 20x: 90.5
#MNB w/ upweight 50x: 90.5
#MNB w/ upweight 200x: 90.0

#No stemming: 90.0
#No lowercasing: 90.0

#No signature: 89.65
#No from: 89.65
