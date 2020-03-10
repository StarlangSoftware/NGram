import Ngram.LaplaceSmoothing;
import Ngram.NGram;
import Ngram.NoSmoothing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TestNGram {

    public static void example(){
        NGram<String> nGram;
        NGram<String> nGram2;
        String[] text1 = {"ali", "topu", "at", "mehmet", "ayşe", "gitti"};
        String[] text2 = {"ali", "top", "at", "ayşe", "gitti"};
        String[] text3 = {"ayşe", "kitabı", "ver"};
        String[] text4 = {"ali", "topu", "mehmete", "at"};
        String[] text5 = {"ali", "topu", "at", "mehmet", "ayşe", "gitti"};
        nGram = new NGram<String>(2);
        nGram.addNGramSentence(text1);
        nGram.addNGramSentence(text2);
        nGram.addNGramSentence(text3);
        nGram.addNGramSentence(text4);
        nGram.addNGramSentence(text5);
        nGram.saveAsText("deneme.txt");
        nGram2 = new NGram<>("deneme.txt");
        nGram2.saveAsText("deneme2.txt");
    }

    public static void saveNGram(){
        NGram<String> nGram = new NGram<String>(2);
        int i = 0;
        try {
            Scanner input = new Scanner(new File("merged.txt"));
            while (input.hasNext()){
                String line = input.nextLine();
                String[] items = line.split(" ");
                i++;
                if (i % 1000000 == 0){
                    System.out.println(i);
                }
                nGram.addNGramSentence(items);
            }
            nGram.saveAsText("model.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        NGram<String> nGram2;
        nGram2 = new NGram<>("model2.txt");
        nGram2.calculateNGramProbabilities(new NoSmoothing<>());
        double p1 = nGram2.getProbability("cam", "bardak");
        System.out.println(p1);
        double p2 = nGram2.getProbability("cam", "ağaç");
        System.out.println(p2);
    }
}
