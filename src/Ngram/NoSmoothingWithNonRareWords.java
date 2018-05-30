package Ngram;

import java.util.HashSet;

public class NoSmoothingWithNonRareWords<Symbol> extends NoSmoothing{
    private HashSet<Symbol> dictionary;
    private double probability;

    public NoSmoothingWithNonRareWords(double probability){
        this.probability = probability;
    }

    protected void setProbabilities(NGram nGram, int level) {
        dictionary = nGram.constructDictionaryWithNonRareWords(level, probability);
        nGram.replaceUnknownWords(dictionary);
        super.setProbabilities(nGram, level);
    }

}
