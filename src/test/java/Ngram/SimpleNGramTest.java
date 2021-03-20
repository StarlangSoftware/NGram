package Ngram;

import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleNGramTest {

    @Test
    public void testProbability() {
        SimpleNGram uniGram, biGram, triGram;
        uniGram = new SimpleNGram("simple1a.txt");
        biGram = new SimpleNGram("simple2a.txt");
        triGram = new SimpleNGram("simple3a.txt");
        assertEquals(5 / 35.0, uniGram.getProbability("<s>"), 0.0);
        assertEquals(1 / 35.0, uniGram.getProbability("kitabı"), 0.0);
        assertEquals(4 / 5.0, biGram.getProbability("<s> ali"), 0.0);
        assertEquals(2 / 4.0, biGram.getProbability("at mehmet"), 0.0);
        assertEquals(1 / 4.0, triGram.getProbability("<s> ali top"), 0.0);
        assertEquals(2 / 3.0, triGram.getProbability("ali topu at"), 0.0);
    }

    @Test
    public void testMultiFileProbability() {
        SimpleNGram uniGram, biGram, triGram;
        uniGram = new SimpleNGram("simple1part1.txt", "simple1part2.txt");
        biGram = new SimpleNGram("simple2part1.txt", "simple2part2.txt", "simple2part3.txt");
        triGram = new SimpleNGram("simple3part1.txt", "simple3part2.txt", "simple3part3.txt", "simple3part4.txt");
        assertEquals(5 / 35.0, uniGram.getProbability("<s>"), 0.0);
        assertEquals(1 / 35.0, uniGram.getProbability("kitabı"), 0.0);
        assertEquals(4 / 5.0, biGram.getProbability("<s> ali"), 0.0);
        assertEquals(2 / 4.0, biGram.getProbability("at mehmet"), 0.0);
        assertEquals(1 / 4.0, triGram.getProbability("<s> ali top"), 0.0);
        assertEquals(2 / 3.0, triGram.getProbability("ali topu at"), 0.0);
    }

}
