package Ngram;

public class NoSmoothing<Symbol> extends SimpleSmoothing<Symbol> {

    /**
     * Calculates the N-Gram probabilities with no smoothing
     * @param nGram N-Gram for which no smoothing is done.
     * @param level Height of the NGram node.
     */
    @Override
    protected void setProbabilities(NGram<Symbol> nGram, int level) {
        nGram.setProbabilityWithPseudoCount(0.0, level);
    }
}
