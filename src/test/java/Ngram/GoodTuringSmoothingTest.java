package Ngram;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GoodTuringSmoothingTest extends SimpleSmoothingTest{

    @Before
    public void setUp() throws Exception{
        super.setUp();
        SimpleSmoothing<String> simpleSmoothing = new GoodTuringSmoothing<>();
        simpleUniGram.calculateNGramProbabilities(simpleSmoothing);
        simpleBiGram.calculateNGramProbabilities(simpleSmoothing);
        simpleTriGram.calculateNGramProbabilities(simpleSmoothing);
        complexUniGram.calculateNGramProbabilities(simpleSmoothing);
        complexBiGram.calculateNGramProbabilities(simpleSmoothing);
        complexTriGram.calculateNGramProbabilities(simpleSmoothing);
    }

    @Test
    public void testPerplexitySimple(){
        assertEquals(14.500734, simpleUniGram.getPerplexity(simpleCorpus), 0.0001);
        assertEquals(2.762526, simpleBiGram.getPerplexity(simpleCorpus), 0.0001);
        assertEquals(3.685001, simpleTriGram.getPerplexity(simpleCorpus), 0.0001);
    }

    @Test
    public void testPerplexityComplex(){
        assertEquals(1290.97916, complexUniGram.getPerplexity(testCorpus), 0.0001);
        assertEquals(8331.518540, complexBiGram.getPerplexity(testCorpus), 0.0001);
        assertEquals(39184.430078, complexTriGram.getPerplexity(testCorpus), 0.0001);
    }

    @Test
    public void testCalculateNGramProbabilitiesSimple() {
        assertEquals(0.116607, simpleUniGram.getProbability("<s>"), 0.0001);
        assertEquals(0.149464, simpleUniGram.getProbability("mahmut"), 0.0001);
        assertEquals(0.026599, simpleUniGram.getProbability("kitabı"), 0.0001);
        assertEquals(0.492147, simpleBiGram.getProbability("<s>", "ali"), 0.0001);
        assertEquals(0.030523, simpleBiGram.getProbability("ayşe", "ali"), 0.0001);
        assertEquals(0.0625, simpleBiGram.getProbability("mahmut", "ali"), 0.0001);
        assertEquals(0.323281, simpleBiGram.getProbability("at", "mehmet"), 0.0001);
        assertEquals(0.049190, simpleTriGram.getProbability("<s>", "ali", "top"), 0.0001);
        assertEquals(0.043874, simpleTriGram.getProbability("ayşe", "kitabı", "at"), 0.0001);
        assertEquals(0.0625, simpleTriGram.getProbability("ayşe", "topu", "at"), 0.0001);
        assertEquals(0.0625, simpleTriGram.getProbability("mahmut", "evde", "kal"), 0.0001);
        assertEquals(0.261463, simpleTriGram.getProbability("ali", "topu", "at"), 0.0001);
    }

    @Test
    public void testCalculateNGramProbabilitiesComplex() {
        assertEquals(0.050745, complexUniGram.getProbability("<s>"), 0.0001);
        assertEquals(0.000126, complexUniGram.getProbability("atatürk"), 0.0001);
        assertEquals(0.000497, complexBiGram.getProbability("<s>", "mustafa"), 0.0001);
        assertEquals(0.014000, complexBiGram.getProbability("mustafa", "kemal"), 0.0001);
        assertEquals(0.061028, complexTriGram.getProbability("<s>", "mustafa", "kemal"), 0.0001);
        assertEquals(0.283532, complexTriGram.getProbability("mustafa", "kemal", "atatürk"), 0.0001);
    }

}