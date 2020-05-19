package Ngram;

import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class SimpleSmoothingTest extends CorpusTest{
    NGram<String> simpleUniGram, simpleBiGram, simpleTriGram;
    NGram<String> complexUniGram, complexBiGram, complexTriGram;
    ArrayList<ArrayList<String>> simpleCorpus;
    ArrayList<ArrayList<String>> trainCorpus, testCorpus, validationCorpus;

    @Before
    public void setUp() throws Exception {
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
}