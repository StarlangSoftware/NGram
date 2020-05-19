package Ngram;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LaplaceSmoothingTest extends SimpleSmoothingTest{

    @Before
    public void setUp() throws Exception{
        super.setUp();
        SimpleSmoothing<String> simpleSmoothing = new LaplaceSmoothing<>();
        simpleUniGram.calculateNGramProbabilities(simpleSmoothing);
        simpleBiGram.calculateNGramProbabilities(simpleSmoothing);
        simpleTriGram.calculateNGramProbabilities(simpleSmoothing);
        complexUniGram.calculateNGramProbabilities(simpleSmoothing);
        complexBiGram.calculateNGramProbabilities(simpleSmoothing);
        complexTriGram.calculateNGramProbabilities(simpleSmoothing);
    }

    @Test
    public void testPerplexitySimple(){
        assertEquals(12.809502, simpleUniGram.getPerplexity(simpleCorpus), 0.0001);
        assertEquals(6.914532, simpleBiGram.getPerplexity(simpleCorpus), 0.0001);
        assertEquals(7.694528, simpleTriGram.getPerplexity(simpleCorpus), 0.0001);
    }

    @Test
    public void testPerplexityComplex(){
        assertEquals(4085.763010, complexUniGram.getPerplexity(testCorpus), 0.0001);
        assertEquals(24763.660225, complexBiGram.getPerplexity(testCorpus), 0.0001);
        assertEquals(49579.187475, complexTriGram.getPerplexity(testCorpus), 0.0001);
    }

    @Test
    public void testCalculateNGramProbabilitiesSimple() {
        assertEquals((5 + 1) / (35 + simpleUniGram.vocabularySize() + 1), simpleUniGram.getProbability("<s>"), 0.0);
        assertEquals((0 + 1) / (35 + simpleUniGram.vocabularySize() + 1), simpleUniGram.getProbability("mahmut"), 0.0);
        assertEquals((1 + 1) / (35 + simpleUniGram.vocabularySize() + 1), simpleUniGram.getProbability("kitabı"), 0.0);
        assertEquals((4 + 1) / (5 + simpleBiGram.vocabularySize() + 1), simpleBiGram.getProbability("<s>", "ali"), 0.0);
        assertEquals((0 + 1) / (2 + simpleBiGram.vocabularySize() + 1), simpleBiGram.getProbability("ayşe", "ali"), 0.0);
        assertEquals(1 / (simpleBiGram.vocabularySize() + 1), simpleBiGram.getProbability("mahmut", "ali"), 0.0);
        assertEquals((2 + 1) / (4 + simpleBiGram.vocabularySize() + 1), simpleBiGram.getProbability("at", "mehmet"), 0.0);
        assertEquals((1 + 1) / (4.0 + simpleTriGram.vocabularySize() + 1), simpleTriGram.getProbability("<s>", "ali", "top"), 0.0);
        assertEquals((0 + 1) / (1.0 + simpleTriGram.vocabularySize() + 1), simpleTriGram.getProbability("ayşe", "kitabı", "at"), 0.0);
        assertEquals(1 / (simpleTriGram.vocabularySize() + 1), simpleTriGram.getProbability("ayşe", "topu", "at"), 0.0);
        assertEquals(1 / (simpleTriGram.vocabularySize() + 1), simpleTriGram.getProbability("mahmut", "evde", "kal"), 0.0);
        assertEquals((2 + 1) / (3.0 + simpleTriGram.vocabularySize() + 1), simpleTriGram.getProbability("ali", "topu", "at"), 0.0);
    }

    @Test
    public void testCalculateNGramProbabilitiesComplex() {
        assertEquals((20000 + 1) / (376019.0 + complexUniGram.vocabularySize() + 1), complexUniGram.getProbability("<s>"), 0.0);
        assertEquals((50 + 1) / (376019.0 + complexUniGram.vocabularySize() + 1), complexUniGram.getProbability("atatürk"), 0.0);
        assertEquals((11 + 1) / (20000.0 + complexBiGram.vocabularySize() + 1), complexBiGram.getProbability("<s>", "mustafa"), 0.0);
        assertEquals((3 + 1) / (138.0 + complexBiGram.vocabularySize() + 1), complexBiGram.getProbability("mustafa", "kemal"), 0.0);
        assertEquals((1 + 1) / (11.0 + complexTriGram.vocabularySize() + 1), complexTriGram.getProbability("<s>", "mustafa", "kemal"), 0.0);
        assertEquals((1 + 1) / (3.0 + complexTriGram.vocabularySize() + 1), complexTriGram.getProbability("mustafa", "kemal", "atatürk"), 0.0);
    }

}