package Ngram;

import Sampling.KFoldCrossValidation;

import java.util.ArrayList;

public class AdditiveSmoothing<Symbol> extends TrainedSmoothing<Symbol> {
    /**
     * Additive pseudocount parameter used in Additive Smoothing. The parameter will be learned using 10-fold cross
     * validation.
     */
    private double delta;

    /**
     * The algorithm tries to optimize the best delta for a given corpus. The algorithm uses perplexity on the validation
     * set as the optimization criterion.
     * @param nGrams 10 N-Grams learned for different folds of the corpus. nGrams[i] is the N-Gram trained with i'th train
     *               fold of the corpus.
     * @param kFoldCrossValidation Cross-validation data used in training and testing the N-grams.
     * @param lowerBound Initial lower bound for optimizing the best delta.
     * @return Best delta optimized with k-fold crossvalidation.
     */
    private double learnBestDelta(NGram<Symbol>[] nGrams, KFoldCrossValidation<ArrayList<Symbol>> kFoldCrossValidation, double lowerBound){
        double bestPerplexity, bestPrevious = -1, upperBound = 1, perplexity, bestDelta = (lowerBound + upperBound) / 2;
        int numberOfParts = 5;
        while (true){
            bestPerplexity = Double.MAX_VALUE;
            for (double value = lowerBound; value <= upperBound; value += (upperBound - lowerBound) / numberOfParts){
                perplexity = 0;
                for (int i = 0; i < 10; i++){
                    nGrams[i].setProbabilityWithPseudoCount(value, nGrams[i].getN());
                    perplexity += nGrams[i].getPerplexity(kFoldCrossValidation.getTestFold(i));
                }
                if (perplexity < bestPerplexity){
                    bestPerplexity = perplexity;
                    bestDelta = value;
                }
            }
            lowerBound = newLowerBound(bestDelta, lowerBound, upperBound, numberOfParts);
            upperBound = newUpperBound(bestDelta, lowerBound, upperBound, numberOfParts);
            if (bestPrevious != -1){
                if (Math.abs(bestPrevious - bestPerplexity) / bestPerplexity < 0.001){
                    break;
                }
            }
            bestPrevious = bestPerplexity;
        }
        return bestDelta;
    }

    /**
     * Wrapper function to learn the parameter (delta) in additive smoothing. The function first creates K NGrams
     * with the train folds of the corpus. Then optimizes delta with respect to the test folds of the corpus.
     * @param corpus Train corpus used to optimize delta parameter
     * @param N N in N-Gram.
     */
    protected void learnParameters(ArrayList corpus, int N) {
        int K = 10;
        NGram<Symbol>[] nGrams = new NGram[K];
        KFoldCrossValidation<ArrayList<Symbol>> kFoldCrossValidation = new KFoldCrossValidation<ArrayList<Symbol>>(corpus, K, 0);
        for (int i = 0; i < K; i++){
            nGrams[i] = new NGram<>(kFoldCrossValidation.getTrainFold(i), N);
        }
        delta = learnBestDelta(nGrams, kFoldCrossValidation, 0.1);
    }

    /**
     * Wrapper function to set the N-gram probabilities with additive smoothing.
     * @param nGram N-Gram for which the probabilities will be set.
     * @param level Level for which N-Gram probabilities will be set. Probabilities for different levels of the
     *              N-gram can be set with this function. If level = 1, N-Gram is treated as UniGram, if level = 2,
     *              N-Gram is treated as Bigram, etc.
     */
    protected void setProbabilities(NGram<Symbol> nGram, int level) {
        nGram.setProbabilityWithPseudoCount(delta, level);
    }

    /**
     * Gets the best delta.
     * @return Learned best delta.
     */
    public double getDelta(){
        return delta;
    }
}
