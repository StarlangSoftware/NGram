package Ngram;

import Sampling.KFoldCrossValidation;

import java.util.ArrayList;

public class InterpolatedSmoothing<Symbol> extends TrainedSmoothing{
    private double lambda1, lambda2;
    private SimpleSmoothing<Symbol> simpleSmoothing;

    /**
     * No argument constructor of {@link InterpolatedSmoothing}
     */
    public InterpolatedSmoothing(){
        this.simpleSmoothing = new GoodTuringSmoothing<Symbol>();
    }

    /**
     * Constructor of {@link InterpolatedSmoothing}
     * @param simpleSmoothing smoothing method.
     */
    public InterpolatedSmoothing(SimpleSmoothing<Symbol> simpleSmoothing){
        this.simpleSmoothing = simpleSmoothing;
    }
    /**
     * The algorithm tries to optimize the best lambda for a given corpus. The algorithm uses perplexity on the validation
     * set as the optimization criterion.
     *
     * @param nGrams 10 N-Grams learned for different folds of the corpus. nGrams[i] is the N-Gram trained with i'th train fold of the corpus.
     * @param kFoldCrossValidation Cross-validation data used in training and testing the N-grams.
     * @param lowerBound Initial lower bound for optimizing the best lambda.
     * @return  Best lambda optimized with k-fold crossvalidation.
     */
    private double learnBestLambda(NGram[] nGrams, KFoldCrossValidation<ArrayList<Symbol>> kFoldCrossValidation, double lowerBound){
        double bestPerplexity, bestPrevious = -1, upperBound = 0.999, perplexity, bestLambda = (lowerBound + upperBound) / 2;
        int numberOfParts = 5;
        ArrayList[] testFolds = new ArrayList[10];
        for (int i = 0; i < 10; i++){
            testFolds[i] = kFoldCrossValidation.getTestFold(i);
        }
        while (true){
            bestPerplexity = Double.MAX_VALUE;
            for (double value = lowerBound; value <= upperBound; value += (upperBound - lowerBound) / numberOfParts){
                perplexity = 0;
                for (int i = 0; i < 10; i++){
                    nGrams[i].setLambda(value);
                    perplexity += nGrams[i].getPerplexity(testFolds[i]);
                }
                if (perplexity < bestPerplexity){
                    bestPerplexity = perplexity;
                    bestLambda = value;
                }
            }
            lowerBound = newLowerBound(bestLambda, lowerBound, upperBound, numberOfParts);
            upperBound = newUpperBound(bestLambda, lowerBound, upperBound, numberOfParts);
            if (bestPrevious != -1){
                if (Math.abs(bestPrevious - bestPerplexity) / bestPerplexity < 0.001){
                    break;
                }
            }
            bestPrevious = bestPerplexity;
        }
        return bestLambda;
    }

    /**
     * The algorithm tries to optimize the best lambdas (lambda1, lambda2) for a given corpus. The algorithm uses perplexity on the validation
     * set as the optimization criterion.
     *
     * @param nGrams 10 N-Grams learned for different folds of the corpus. nGrams[i] is the N-Gram trained with i'th train fold of the corpus.
     * @param kFoldCrossValidation Cross-validation data used in training and testing the N-grams.
     * @param lowerBound1 Initial lower bound for optimizing the best lambda1.
     * @param lowerBound2 Initial lower bound for optimizing the best lambda2.
     * @return
     */
    private double[] learnBestLambdas(NGram[] nGrams, KFoldCrossValidation<ArrayList<Symbol>> kFoldCrossValidation, double lowerBound1, double lowerBound2){
        double bestPerplexity, upperBound1 = 0.999, upperBound2 = 0.999, bestPrevious = -1, perplexity, bestLambda1 = (lowerBound1 + upperBound1) / 2, bestLambda2 = (lowerBound1 + upperBound1) / 2;
        ArrayList[] testFolds = new ArrayList[10];
        int numberOfParts = 5;
        for (int i = 0; i < 10; i++){
            testFolds[i] = kFoldCrossValidation.getTestFold(i);
        }
        while (true){
            bestPerplexity = Double.MAX_VALUE;
            for (double value1 = lowerBound1; value1 <= upperBound1; value1 += (upperBound1 - lowerBound1) / numberOfParts){
                for (double value2 = lowerBound2; value2 <= upperBound2 && value1 + value2 < 1; value2 += (upperBound2 - lowerBound2) / numberOfParts){
                    perplexity = 0;
                    for (int i = 0; i < 10; i++){
                        nGrams[i].setLambda(value1, value2);
                        perplexity += nGrams[i].getPerplexity(testFolds[i]);
                    }
                    if (perplexity < bestPerplexity){
                        bestPerplexity = perplexity;
                        bestLambda1 = value1;
                        bestLambda2 = value2;
                    }
                }
            }
            lowerBound1 = newLowerBound(bestLambda1, lowerBound1, upperBound1, numberOfParts);
            upperBound1 = newUpperBound(bestLambda1, lowerBound1, upperBound1, numberOfParts);
            lowerBound2 = newLowerBound(bestLambda2, lowerBound2, upperBound2, numberOfParts);
            upperBound2 = newUpperBound(bestLambda2, lowerBound2, upperBound2, numberOfParts);
            if (bestPrevious != -1){
                if (Math.abs(bestPrevious - bestPerplexity) / bestPerplexity < 0.001){
                    break;
                }
            }
            bestPrevious = bestPerplexity;
        }
        return new double[]{bestLambda1, bestLambda2};
    }

    /**
     * Wrapper function to learn the parameters (lambda1 and lambda2) in interpolated smoothing. The function first creates K NGrams
     * with the train folds of the corpus. Then optimizes lambdas with respect to the test folds of the corpus depending on given N.
     * @param corpus Train corpus used to optimize lambda parameters
     * @param N N in N-Gram.
     */
    @Override
    protected void learnParameters(ArrayList corpus, int N) {
        if (N <= 1){
            return;
        }
        int K = 10;
        NGram[] nGrams = new NGram[K];
        KFoldCrossValidation<ArrayList<Symbol>> kFoldCrossValidation = new KFoldCrossValidation<>(corpus, K, 0);
        for (int i = 0; i < K; i++){
            nGrams[i] = new NGram<Symbol>(kFoldCrossValidation.getTrainFold(i), N);
            for (int j = 2; j<= N; j++){
                nGrams[i].calculateNGramProbabilities(simpleSmoothing, j);
            }
            nGrams[i].calculateNGramProbabilities(simpleSmoothing, 1);
        }
        if (N == 2){
            lambda1 = learnBestLambda(nGrams, kFoldCrossValidation, 0.1);
        } else {
            if (N == 3){
                double[] bestLambdas = learnBestLambdas(nGrams, kFoldCrossValidation, 0.1, 0.1);
                lambda1 = bestLambdas[0];
                lambda2 = bestLambdas[1];
            }
        }
    }
    /**
     * Wrapper function to set the N-gram probabilities with interpolated smoothing.
     * @param nGram N-Gram for which the probabilities will be set.
     * @param level Level for which N-Gram probabilities will be set. Probabilities for different levels of the
     *              N-gram can be set with this function. If level = 1, N-Gram is treated as UniGram, if level = 2,
     *              N-Gram is treated as Bigram, etc.
     *
     */
    @Override
    protected void setProbabilities(NGram nGram, int level) {
        for (int j = 2; j<= nGram.getN(); j++){
            nGram.calculateNGramProbabilities(simpleSmoothing, j);
        }
        nGram.calculateNGramProbabilities(simpleSmoothing, 1);
        switch (nGram.getN()){
            case 2:
                nGram.setLambda(lambda1);
                break;
            case 3:
                nGram.setLambda(lambda1, lambda2);
                break;
        }
    }
}
