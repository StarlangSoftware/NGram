package Ngram;

public abstract class SimpleSmoothing<Symbol> {
    protected abstract void setProbabilities(NGram<Symbol> nGram, int level);

    /**
     * Calculates the N-Gram probabilities with simple smoothing.
     * @param nGram N-Gram for which simple smoothing calculation is done.
     */
    protected void setProbabilities(NGram<Symbol> nGram){
        setProbabilities(nGram, nGram.getN());
    }
}
