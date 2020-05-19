package Ngram;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class CorpusTest {

    protected ArrayList<ArrayList<String>> readCorpus(String fileName){
        ArrayList<ArrayList<String>> corpus = new ArrayList<>();
        try {
            Scanner input = new Scanner(new File(fileName));
            while (input.hasNextLine()){
                String[] words = input.nextLine().split(" ");
                ArrayList<String> line = new ArrayList<>();
                Collections.addAll(line, words);
                corpus.add(line);
            }
            input.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return corpus;
    }

}
