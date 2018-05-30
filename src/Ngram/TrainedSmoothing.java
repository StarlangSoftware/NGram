package Ngram;

import java.util.ArrayList;

public abstract class TrainedSmoothing<Symbol> extends SimpleSmoothing<Symbol>{
    protected abstract void learnParameters(ArrayList<ArrayList<Symbol>> corpus, int N);

    protected double newLowerBound(double current, double currentLowerBound, double currentUpperBound, int numberOfParts){
        if (current != currentLowerBound){
            return current - (currentUpperBound - currentLowerBound) / numberOfParts;
        } else {
            return current / numberOfParts;
        }
    }

    protected double newUpperBound(double current, double currentLowerBound, double currentUpperBound, int numberOfParts){
        if (current != currentUpperBound){
            return current + (currentUpperBound - currentLowerBound) / numberOfParts;
        } else {
            return current * numberOfParts;
        }
    }

    public void train(ArrayList<ArrayList<Symbol>> corpus, NGram<Symbol> nGram){
        learnParameters(corpus, nGram.getN());
        setProbabilities(nGram);
    }

}
