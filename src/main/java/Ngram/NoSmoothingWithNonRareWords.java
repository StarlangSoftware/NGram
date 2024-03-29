package Ngram;

import java.util.HashSet;

public class NoSmoothingWithNonRareWords<Symbol> extends NoSmoothing<Symbol>{
    private final double probability;

    /**
     * Constructor of {@link NoSmoothingWithNonRareWords}
     *
     * @param probability Setter for the probability.
     */
    public NoSmoothingWithNonRareWords(double probability){
        this.probability = probability;
    }

    /**
     * Wrapper function to set the N-gram probabilities with no smoothing and replacing unknown words not found in nonrare words.
     * @param nGram N-Gram for which the probabilities will be set.
     * @param level Level for which N-Gram probabilities will be set. Probabilities for different levels of the
     *              N-gram can be set with this function. If level = 1, N-Gram is treated as UniGram, if level = 2,
     *              N-Gram is treated as Bigram, etc.
     *
     */
    protected void setProbabilities(NGram<Symbol> nGram, int level) {
        HashSet<Symbol> dictionary = nGram.constructDictionaryWithNonRareWords(level, probability);
        nGram.replaceUnknownWords(dictionary);
        super.setProbabilities(nGram, level);
    }

}
