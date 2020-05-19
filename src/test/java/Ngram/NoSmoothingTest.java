package Ngram;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NoSmoothingTest extends SimpleSmoothingTest{

    @Before
    public void setUp() throws Exception{
        super.setUp();
        SimpleSmoothing<String> simpleSmoothing = new NoSmoothing<>();
        simpleUniGram.calculateNGramProbabilities(simpleSmoothing);
        simpleBiGram.calculateNGramProbabilities(simpleSmoothing);
        simpleTriGram.calculateNGramProbabilities(simpleSmoothing);
        complexUniGram.calculateNGramProbabilities(simpleSmoothing);
        complexBiGram.calculateNGramProbabilities(simpleSmoothing);
        complexTriGram.calculateNGramProbabilities(simpleSmoothing);
    }

    @Test
    public void testPerplexitySimple(){
        assertEquals(12.318362, simpleUniGram.getPerplexity(simpleCorpus), 0.0001);
        assertEquals(1.573148, simpleBiGram.getPerplexity(simpleCorpus), 0.0001);
        assertEquals(1.248330, simpleTriGram.getPerplexity(simpleCorpus), 0.0001);
    }

    @Test
    public void testPerplexityComplex(){
        assertEquals(3220.299369, complexUniGram.getPerplexity(trainCorpus), 0.0001);
        assertEquals(32.362912, complexBiGram.getPerplexity(trainCorpus), 0.0001);
        assertEquals(2.025259, complexTriGram.getPerplexity(trainCorpus), 0.0001);
    }

    @Test
    public void testCalculateNGramProbabilitiesSimple() {
        assertEquals(5 / 35.0, simpleUniGram.getProbability("<s>"), 0.0);
        assertEquals(0.0, simpleUniGram.getProbability("mahmut"), 0.0);
        assertEquals(1.0 / 35.0, simpleUniGram.getProbability("kitabı"), 0.0);
        assertEquals(4 / 5.0, simpleBiGram.getProbability("<s>", "ali"), 0.0);
        assertEquals(0 / 2.0, simpleBiGram.getProbability("ayşe", "ali"), 0.0);
        assertEquals(0.0, simpleBiGram.getProbability("mahmut", "ali"), 0.0);
        assertEquals(2 / 4.0, simpleBiGram.getProbability("at", "mehmet"), 0.0);
        assertEquals(1 / 4.0, simpleTriGram.getProbability("<s>", "ali", "top"), 0.0);
        assertEquals(0 / 1.0, simpleTriGram.getProbability("ayşe", "kitabı", "at"), 0.0);
        assertEquals(0.0, simpleTriGram.getProbability("ayşe", "topu", "at"), 0.0);
        assertEquals(0.0, simpleTriGram.getProbability("mahmut", "evde", "kal"), 0.0);
        assertEquals(2 / 3.0, simpleTriGram.getProbability("ali", "topu", "at"), 0.0);
    }

    @Test
    public void testCalculateNGramProbabilitiesComplex() {
        assertEquals(20000 / 376019.0, complexUniGram.getProbability("<s>"), 0.0);
        assertEquals(50 / 376019.0, complexUniGram.getProbability("atatürk"), 0.0);
        assertEquals(11 / 20000.0, complexBiGram.getProbability("<s>", "mustafa"), 0.0);
        assertEquals(3 / 138.0, complexBiGram.getProbability("mustafa", "kemal"), 0.0);
        assertEquals(1 / 11.0, complexTriGram.getProbability("<s>", "mustafa", "kemal"), 0.0);
        assertEquals(1 / 3.0, complexTriGram.getProbability("mustafa", "kemal", "atatürk"), 0.0);
    }

}