package Ngram;

public class LaplaceSmoothing<Symbol> extends SimpleSmoothing<Symbol> {
    private final double delta;

    /**
     * Constructor for Laplace smoothing. Sets the delta to 1.0
     */
    public LaplaceSmoothing(){
        delta = 1.0;
    }

    /**
     * Constructor for Laplace smoothing. Sets the delta.
     * @param delta Delta value in Laplace smoothing.
     */
    public LaplaceSmoothing(double delta){
        this.delta = delta;
    }

    /**
     * Wrapper function to set the N-gram probabilities with laplace smoothing.
     *
     * @param nGram N-Gram for which the probabilities will be set.
     * @param level Level for which N-Gram probabilities will be set. Probabilities for different levels of the
     *              N-gram can be set with this function. If level = 1, N-Gram is treated as UniGram, if level = 2,
     *              N-Gram is treated as Bigram, etc.
     */
    @Override
    protected void setProbabilities(NGram<Symbol> nGram, int level) {
        nGram.setProbabilityWithPseudoCount(delta, level);
    }
}
