package Ngram;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AdditiveSmoothingTest extends SimpleSmoothingTest{
    double delta1, delta2, delta3;

    @Before
    public void setUp() throws Exception{
        super.setUp();
        AdditiveSmoothing<String> additiveSmoothing = new AdditiveSmoothing<String>();
        complexUniGram.calculateNGramProbabilities(validationCorpus, additiveSmoothing);
        delta1 = additiveSmoothing.getDelta();
        complexBiGram.calculateNGramProbabilities(validationCorpus, additiveSmoothing);
        delta2 = additiveSmoothing.getDelta();
        complexTriGram.calculateNGramProbabilities(validationCorpus, additiveSmoothing);
        delta3 = additiveSmoothing.getDelta();
    }

    @Test
    public void testPerplexityComplex(){
        assertEquals(4043.947022, complexUniGram.getPerplexity(testCorpus), 0.0001);
        assertEquals(9220.218871, complexBiGram.getPerplexity(testCorpus), 0.0001);
        assertEquals(30695.701941, complexTriGram.getPerplexity(testCorpus), 0.0001);
    }

    @Test
    public void testCalculateNGramProbabilitiesComplex() {
        assertEquals((20000 + delta1) / (376019.0 + delta1 * (complexUniGram.vocabularySize() + 1)), complexUniGram.getProbability("<s>"), 0.0);
        assertEquals((50 + delta1) / (376019.0 + delta1 * (complexUniGram.vocabularySize() + 1)), complexUniGram.getProbability("atatürk"), 0.0);
        assertEquals((11 + delta2) / (20000.0 + delta2 * (complexBiGram.vocabularySize() + 1)), complexBiGram.getProbability("<s>", "mustafa"), 0.0);
        assertEquals((3 + delta2) / (138.0 + delta2 * (complexBiGram.vocabularySize() + 1)), complexBiGram.getProbability("mustafa", "kemal"), 0.0);
        assertEquals((1 + delta3) / (11.0 + delta3 * (complexTriGram.vocabularySize() + 1)), complexTriGram.getProbability("<s>", "mustafa", "kemal"), 0.0);
        assertEquals((1 + delta3) / (3.0 + delta3 * (complexTriGram.vocabularySize() + 1)), complexTriGram.getProbability("mustafa", "kemal", "atatürk"), 0.0);
    }

}