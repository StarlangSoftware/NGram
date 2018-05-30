package Ngram;

public class LaplaceSmoothing<Symbol> extends SimpleSmoothing {
    private double delta;

    public LaplaceSmoothing(){
        delta = 1.0;
    }

    public LaplaceSmoothing(double delta){
        this.delta = delta;
    }

    @Override
    protected void setProbabilities(NGram nGram, int level) {
        nGram.setProbabilityWithPseudoCount(delta, level);
    }
}
