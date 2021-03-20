package Ngram;

import Util.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class SimpleNGram {

    private HashMap<String, Double> probabilities;

    public SimpleNGram(String fileName){
        probabilities = new HashMap<>();
        InputStream inputStream = FileUtils.getInputStream(fileName);
        if (inputStream != null){
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                readHeader(br);
                readRecursive(true, br, "", 0);
                br.close();
            } catch (IOException e) {
            }
        }
    }

    public SimpleNGram(String... fileNameList){
        probabilities = new HashMap<>();
        MultipleFile multipleFile = new MultipleFile(fileNameList);
        readHeader(multipleFile.getBufferedReader());
        readRecursive(true, multipleFile, "", 0);
        multipleFile.close();
    }

    private void readHeader(BufferedReader br){
        int vocabularySize;
        try {
            br.readLine();
            br.readLine();
            vocabularySize = Integer.parseInt(br.readLine());
            for (int i = 0; i < vocabularySize; i++){
                br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readRecursive(boolean isRootNode, BufferedReader br, String currentHistory, int parentCount) {
        try {
            if (!isRootNode) {
                if (currentHistory.isEmpty()){
                    currentHistory = br.readLine().trim();
                } else {
                    currentHistory += " " + br.readLine().trim();
                }
            }
            String line = br.readLine().trim();
            String[] items = line.split(" ");
            if (items.length != 4){
                System.out.println("Error in line -> " + line);
            }
            int count = Integer.parseInt(items[0]);
            int numberOfChildren = Integer.parseInt(items[3]);
            if (numberOfChildren > 0){
                for (int i = 0; i < numberOfChildren; i++){
                    readRecursive(false, br, currentHistory, count);
                }
            } else {
                probabilities.put(currentHistory, count / (parentCount + 0.0));
            }
        } catch (IOException e) {
        }
    }

    private void readRecursive(boolean isRootNode, MultipleFile multipleFile, String currentHistory, int parentCount) {
        if (!isRootNode) {
            if (currentHistory.isEmpty()){
                currentHistory = multipleFile.readLine().trim();
            } else {
                currentHistory += " " + multipleFile.readLine().trim();
            }
        }
        String line = multipleFile.readLine().trim();
        String[] items = line.split(" ");
        if (items.length != 4){
            System.out.println("Error in line -> " + line);
        }
        int count = Integer.parseInt(items[0]);
        int numberOfChildren = Integer.parseInt(items[3]);
        if (numberOfChildren > 0){
            for (int i = 0; i < numberOfChildren; i++){
                readRecursive(false, multipleFile, currentHistory, count);
            }
        } else {
            probabilities.put(currentHistory, count / (parentCount + 0.0));
        }
    }

    public double getProbability(String nGram){
        if (probabilities.containsKey(nGram)){
            return probabilities.get(nGram);
        } else {
            return 0.0;
        }
    }
}
