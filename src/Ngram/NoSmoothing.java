package Ngram;

public class NoSmoothing<Symbol> extends SimpleSmoothing {

    @Override
    protected void setProbabilities(NGram nGram, int level) {
        nGram.setProbabilityWithPseudoCount(0.0, level);
    }
}
