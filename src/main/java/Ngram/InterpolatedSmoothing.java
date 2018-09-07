package Ngram;

import Sampling.KFoldCrossValidation;

import java.util.ArrayList;

public class InterpolatedSmoothing<Symbol> extends TrainedSmoothing{
    private double lambda1, lambda2;
    private SimpleSmoothing<Symbol> simpleSmoothing;

    public InterpolatedSmoothing(){
        this.simpleSmoothing = new GoodTuringSmoothing<Symbol>();
    }

    public InterpolatedSmoothing(SimpleSmoothing<Symbol> simpleSmoothing){
        this.simpleSmoothing = simpleSmoothing;
    }

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
