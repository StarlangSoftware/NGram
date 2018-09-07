package Ngram;

public abstract class SimpleSmoothing<Symbol> {
    protected abstract void setProbabilities(NGram<Symbol> nGram, int level);

    protected void setProbabilities(NGram<Symbol> nGram){
        setProbabilities(nGram, nGram.getN());
    }
}
