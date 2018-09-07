package Ngram;

import Math.*;

import java.util.ArrayList;

public class GoodTuringSmoothing<Symbol> extends SimpleSmoothing {

    /**
     * Given counts of counts, this function will calculate the estimated counts of counts c$^*$ with
     * Good-Turing smoothing. First, the algorithm filters the non-zero counts from counts of counts array and constructs
     * c and r arrays. Then it constructs Z_n array with Z_n = (2C_n / (r_{n+1} - r_{n-1})). The algorithm then uses
     * simple linear regression on Z_n values to estimate w_1 and w_0, where log(N[i]) = w_1log(i) + w_0
     * @param countsOfCounts Counts of counts. countsOfCounts[1] is the number of words occurred once in the corpus.
     *                       countsOfCounts[i] is the number of words occurred i times in the corpus.
     * @return Estimated counts of counts array. N[1] is the estimated count for out of vocabulary words.
     */
    private double[] linearRegressionOnCountsOfCounts(int[] countsOfCounts){
        double[] N = new double[countsOfCounts.length];
        ArrayList<Integer> r = new ArrayList<Integer>();
        ArrayList<Integer> c = new ArrayList<Integer>();
        double xt, rt;
        for (int i = 1; i < countsOfCounts.length; i++) {
            if (countsOfCounts[i] != 0) {
                r.add(i);
                c.add(countsOfCounts[i]);
            }
        }
        Matrix A = new Matrix(2, 2);
        Vector y = new Vector(2, 0);
        for (int i = 0; i < r.size(); i++){
            xt = Math.log(r.get(i));
            if (i == 0){
                rt = Math.log(c.get(i));
            } else {
                if (i == r.size() - 1){
                    rt = Math.log((1.0 * c.get(i)) / (r.get(i) - r.get(i - 1)));
                } else {
                    rt = Math.log((2.0 * c.get(i)) / (r.get(i + 1) - r.get(i - 1)));
                }
            }
            A.addValue(0, 0, 1.0);
            A.addValue(0, 1, xt);
            A.addValue(1, 0, xt);
            A.addValue(1, 1, xt * xt);
            y.addValue(0, rt);
            y.addValue(1, rt * xt);
        }
        try {
            A.inverse();
            Vector w = A.multiplyWithVectorFromRight(y);
            double w0 = w.getValue(0);
            double w1 = w.getValue(1);
            for (int i = 1; i < countsOfCounts.length; i++){
                N[i] = Math.exp(Math.log(i) * w1 + w0);
            }
        } catch (DeterminantZero | MatrixColumnMismatch columnMismatch) {
        }
        return N;
    }

    /**
     * Wrapper function to set the N-gram probabilities with Good-Turing smoothing. N[1] / \sum_{i=1}^infty N_i is
     * the out of vocabulary probability.
     * @param nGram N-Gram for which the probabilities will be set.
     * @param level Level for which N-Gram probabilities will be set. Probabilities for different levels of the
     *              N-gram can be set with this function. If level = 1, N-Gram is treated as UniGram, if level = 2,
     *              N-Gram is treated as Bigram, etc.
     */
    protected void setProbabilities(NGram nGram, int level) {
        int[] countsOfCounts = nGram.calculateCountsOfCounts(level);
        double[] N = linearRegressionOnCountsOfCounts(countsOfCounts);
        double sum = 0;
        for (int r = 1; r < countsOfCounts.length; r++){
            sum += countsOfCounts[r] * r;
        }
        nGram.setAdjustedProbability(N, level, N[1] / sum);
    }
}
