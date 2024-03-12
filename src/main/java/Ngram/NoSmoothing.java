package Ngram;

public class NoSmoothing<Symbol> extends SimpleSmoothing<Symbol> {

    @Override
    protected void setProbabilities(NGram<Symbol> nGram, int level) {
        nGram.setProbabilityWithPseudoCount(0.0, level);
    }
}
