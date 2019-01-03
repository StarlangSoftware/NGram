package Ngram;

import java.util.ArrayList;

public abstract class TrainedSmoothing<Symbol> extends SimpleSmoothing<Symbol>{

    protected abstract void learnParameters(ArrayList<ArrayList<Symbol>> corpus, int N);

    /**
     * Calculates new lower bound.
     * @param current current value.
     * @param currentLowerBound current lower bound
     * @param currentUpperBound current upper bound
     * @param numberOfParts number of parts between lower and upper bound.
     * @return new lower bound
     */
    protected double newLowerBound(double current, double currentLowerBound, double currentUpperBound, int numberOfParts){
        if (current != currentLowerBound){
            return current - (currentUpperBound - currentLowerBound) / numberOfParts;
        } else {
            return current / numberOfParts;
        }
    }

    /**
     * Calculates new upper bound.
     * @param current current value.
     * @param currentLowerBound current lower bound
     * @param currentUpperBound current upper bound
     * @param numberOfParts number of parts between lower and upper bound.
     * @return new upper bound
     */
    protected double newUpperBound(double current, double currentLowerBound, double currentUpperBound, int numberOfParts){
        if (current != currentUpperBound){
            return current + (currentUpperBound - currentLowerBound) / numberOfParts;
        } else {
            return current * numberOfParts;
        }
    }

    /**
     * Wrapper function to learn parameters of the smoothing method and set the N-gram probabilities.
     *
     * @param corpus Train corpus used to optimize parameters of the smoothing method.
     * @param nGram N-Gram for which the probabilities will be set.
     */
    public void train(ArrayList<ArrayList<Symbol>> corpus, NGram<Symbol> nGram){
        learnParameters(corpus, nGram.getN());
        setProbabilities(nGram);
    }

}
