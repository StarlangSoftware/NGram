package Ngram;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class NGramTest extends CorpusTest{
    ArrayList<ArrayList<String>> simpleCorpus, trainCorpus, testCorpus, validationCorpus;
    NGram<String> simpleUniGram, simpleBiGram, simpleTriGram;
    NGram<String> complexUniGram, complexBiGram, complexTriGram;

    @Before
    public void setUp() {
        ArrayList<String> text1 = new ArrayList<>(Arrays.asList("<s>", "ali", "topu", "at", "mehmet", "ayşeye", "gitti", "</s>"));
        ArrayList<String> text2 = new ArrayList<>(Arrays.asList("<s>", "ali", "top", "at", "ayşe", "eve", "gitti", "</s>"));
        ArrayList<String> text3 = new ArrayList<>(Arrays.asList("<s>", "ayşe", "kitabı", "ver", "</s>"));
        ArrayList<String> text4 = new ArrayList<>(Arrays.asList("<s>", "ali", "topu", "mehmete", "at", "</s>"));
        ArrayList<String> text5 = new ArrayList<>(Arrays.asList("<s>", "ali", "topu", "at", "mehmet", "ayşeyle", "gitti", "</s>"));
        simpleCorpus = new ArrayList<>(Arrays.asList(text1, text2, text3, text4, text5));
        simpleUniGram = new NGram<>(simpleCorpus, 1);
        simpleBiGram = new NGram<>(simpleCorpus, 2);
        simpleTriGram = new NGram<>(simpleCorpus, 3);
        trainCorpus = readCorpus("train.txt");
        complexUniGram = new NGram<>(trainCorpus, 1);
        complexBiGram = new NGram<>(trainCorpus, 2);
        complexTriGram = new NGram<>(trainCorpus, 3);
        testCorpus = readCorpus("test.txt");
        validationCorpus = readCorpus("validation.txt");
    }

    @Test
    public void testGetCountSimple() {
        assertEquals(5, simpleUniGram.getCount(new String[]{"<s>"}), 0.0);
        assertEquals(0, simpleUniGram.getCount(new String[]{"mahmut"}), 0.0);
        assertEquals(1, simpleUniGram.getCount(new String[]{"kitabı"}), 0.0);
        assertEquals(4, simpleBiGram.getCount(new String[]{"<s>", "ali"}), 0.0);
        assertEquals(0, simpleBiGram.getCount(new String[]{"ayşe", "ali"}), 0.0);
        assertEquals(0, simpleBiGram.getCount(new String[]{"mahmut", "ali"}), 0.0);
        assertEquals(2, simpleBiGram.getCount(new String[]{"at", "mehmet"}), 0.0);
        assertEquals(1, simpleTriGram.getCount(new String[]{"<s>", "ali", "top"}), 0.0);
        assertEquals(0, simpleTriGram.getCount(new String[]{"ayşe", "kitabı", "at"}), 0.0);
        assertEquals(0, simpleTriGram.getCount(new String[]{"ayşe", "topu", "at"}), 0.0);
        assertEquals(0, simpleTriGram.getCount(new String[]{"mahmut", "evde", "kal"}), 0.0);
        assertEquals(2, simpleTriGram.getCount(new String[]{"ali", "topu", "at"}), 0.0);
    }

    @Test
    public void testGetCountComplex() {
        assertEquals(20000, complexUniGram.getCount(new String[]{"<s>"}), 0.0);
        assertEquals(50, complexUniGram.getCount(new String[]{"atatürk"}), 0.0);
        assertEquals(11, complexBiGram.getCount(new String[]{"<s>", "mustafa"}), 0.0);
        assertEquals(3, complexBiGram.getCount(new String[]{"mustafa", "kemal"}), 0.0);
        assertEquals(1, complexTriGram.getCount(new String[]{"<s>", "mustafa", "kemal"}), 0.0);
        assertEquals(1, complexTriGram.getCount(new String[]{"mustafa", "kemal", "atatürk"}), 0.0);
    }

    @Test
    public void testVocabularySizeSimple(){
        assertEquals(15, simpleUniGram.vocabularySize(), 0.0);
    }

    @Test
    public void testVocabularySizeComplex(){
        assertEquals(57625, complexUniGram.vocabularySize(), 0.0);
        complexUniGram = new NGram<>(testCorpus, 1);
        assertEquals(55485, complexUniGram.vocabularySize(), 0.0);
        complexUniGram = new NGram<>(validationCorpus, 1);
        assertEquals(35663, complexUniGram.vocabularySize(), 0.0);
    }

    @Test
    public void testPrune(){
        simpleBiGram.prune(0.0);
        assertEquals(4, simpleBiGram.getCount(new String[]{"<s>", "ali"}), 0.0);
        assertEquals(1, simpleBiGram.getCount(new String[]{"<s>", "ayşe"}), 0.0);
        assertEquals(3, simpleBiGram.getCount(new String[]{"ali", "topu"}), 0.0);
        assertEquals(1, simpleBiGram.getCount(new String[]{"ali", "top"}), 0.0);
        assertEquals(2, simpleBiGram.getCount(new String[]{"topu", "at"}), 0.0);
        assertEquals(1, simpleBiGram.getCount(new String[]{"topu", "mehmete"}), 0.0);
        simpleBiGram.prune(0.6);
        assertEquals(4, simpleBiGram.getCount(new String[]{"<s>", "ali"}), 0.0);
        assertEquals(0, simpleBiGram.getCount(new String[]{"<s>", "ayşe"}), 0.0);
        assertEquals(3, simpleBiGram.getCount(new String[]{"ali", "topu"}), 0.0);
        assertEquals(0, simpleBiGram.getCount(new String[]{"ali", "top"}), 0.0);
        assertEquals(2, simpleBiGram.getCount(new String[]{"topu", "at"}), 0.0);
        assertEquals(0, simpleBiGram.getCount(new String[]{"topu", "mehmete"}), 0.0);
        simpleBiGram.prune(0.7);
        assertEquals(4, simpleBiGram.getCount(new String[]{"<s>", "ali"}), 0.0);
        assertEquals(3, simpleBiGram.getCount(new String[]{"ali", "topu"}), 0.0);
        assertEquals(0, simpleBiGram.getCount(new String[]{"topu", "at"}), 0.0);
        simpleBiGram.prune(0.8);
        assertEquals(4, simpleBiGram.getCount(new String[]{"<s>", "ali"}), 0.0);
        assertEquals(0, simpleBiGram.getCount(new String[]{"ali", "topu"}), 0.0);
        simpleBiGram.prune(0.9);
        assertEquals(0, simpleBiGram.getCount(new String[]{"<s>", "ali"}), 0.0);
    }

    @Test
    public void testSaveAsText(){
        simpleUniGram.saveAsText("simple1.txt");
        simpleBiGram.saveAsText("simple2.txt");
        simpleTriGram.saveAsText("simple3.txt");
    }

    @Test
    public void testLoadMultiPart(){
        simpleUniGram = new NGram<String>("simple1part1.txt", "simple1part2.txt");
        simpleBiGram = new NGram<String>("simple2part1.txt", "simple2part2.txt", "simple2part3.txt");
        simpleTriGram = new NGram<String>("simple3part1.txt", "simple3part2.txt", "simple3part3.txt", "simple3part4.txt");
        testGetCountSimple();
        testVocabularySizeSimple();
    }

}