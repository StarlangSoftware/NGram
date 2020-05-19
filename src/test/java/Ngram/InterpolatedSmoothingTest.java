package Ngram;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InterpolatedSmoothingTest extends SimpleSmoothingTest{
    InterpolatedSmoothing<String> interpolatedSmoothing;

    @Before
    public void setUp() throws Exception{
        super.setUp();
        interpolatedSmoothing = new InterpolatedSmoothing<>();
        complexBiGram.calculateNGramProbabilities(validationCorpus, interpolatedSmoothing);
        complexTriGram.calculateNGramProbabilities(validationCorpus, interpolatedSmoothing);
    }

    @Test
    public void testPerplexityComplex(){
        assertEquals(917.214864, complexBiGram.getPerplexity(testCorpus), 0.0001);
        assertEquals(3000.451177, complexTriGram.getPerplexity(testCorpus), 0.0001);
    }

    @Test
    public void testCalculateNGramProbabilitiesComplex() {
        assertEquals(0.000418, complexBiGram.getProbability("<s>", "mustafa"), 0.0001);
        assertEquals(0.005555, complexBiGram.getProbability("mustafa", "kemal"), 0.0001);
        assertEquals(0.014406, complexTriGram.getProbability("<s>", "mustafa", "kemal"), 0.0001);
        assertEquals(0.058765, complexTriGram.getProbability("mustafa", "kemal", "atatürk"), 0.0001);
    }

}