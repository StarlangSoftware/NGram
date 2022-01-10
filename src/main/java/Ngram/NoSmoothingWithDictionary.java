package Ngram;

import java.util.HashSet;

public class NoSmoothingWithDictionary<Symbol> extends NoSmoothing<Symbol>{
    private HashSet<Symbol> dictionary;

    /**
     * Constructor of {@link NoSmoothingWithDictionary}
     * @param dictionary Dictionary to use in smoothing
     */
    public NoSmoothingWithDictionary(HashSet<Symbol> dictionary){
        this.dictionary = dictionary;
    }

    /**
     * Wrapper function to set the N-gram probabilities with no smoothing and replacing unknown words not found in {@link HashSet} the dictionary.
     * @param nGram N-Gram for which the probabilities will be set.
     * @param level Level for which N-Gram probabilities will be set. Probabilities for different levels of the
     *              N-gram can be set with this function. If level = 1, N-Gram is treated as UniGram, if level = 2,
     *              N-Gram is treated as Bigram, etc.
     */
    protected void setProbabilities(NGram nGram, int level) {
        nGram.replaceUnknownWords(dictionary);
        super.setProbabilities(nGram, level);
    }
}
