package Ngram;

import java.util.HashSet;

public class NoSmoothingWithDictionary<Symbol> extends NoSmoothing{
    private HashSet<Symbol> dictionary;

    public NoSmoothingWithDictionary(HashSet<Symbol> dictionary){
        this.dictionary = dictionary;
    }

    protected void setProbabilities(NGram nGram, int level) {
        nGram.replaceUnknownWords(dictionary);
        super.setProbabilities(nGram, level);
    }
}
