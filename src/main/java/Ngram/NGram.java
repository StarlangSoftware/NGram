package Ngram;

import DataStructure.CounterHashMap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

public class NGram<Symbol> implements Serializable{

    private NGramNode<Symbol> rootNode;
    private int N;
    private double lambda1, lambda2;
    private boolean interpolated = false;
    private HashSet<Symbol> vocabulary;
    private double[] probabilityOfUnseen;

    public NGram(ArrayList<ArrayList<Symbol>> corpus, int N){
        int i;
        this.N = N;
        this.vocabulary = new HashSet<>();
        probabilityOfUnseen = new double[N];
        rootNode = new NGramNode<Symbol>(null);
        for (i = 0; i < corpus.size(); i++)
            addNGramSentence((Symbol[]) corpus.get(i).toArray());
    }

    public NGram(int N){
        this.N = N;
        this.vocabulary = new HashSet<>();
        probabilityOfUnseen = new double[N];
        rootNode = new NGramNode<Symbol>(null);
    }

    public int getN(){
        return N;
    }

    public void setN(int N){
        this.N = N;
    }

    public void addNGram(Symbol[] symbols){
        for (Symbol s : symbols){
            vocabulary.add(s);
        }
        rootNode.addNGram(symbols, 0, N);
    }

    public void addNGramSentence(Symbol[] symbols){
        for (Symbol s : symbols){
            vocabulary.add(s);
        }
        for (int j = 0; j < symbols.length - N + 1; j++){
            rootNode.addNGram(symbols, j, N);
        }
    }

    public double vocabularySize(){
        return vocabulary.size();
    }

    public void setLambda(double lambda1){
        if (N == 2){
            interpolated = true;
            this.lambda1 = lambda1;
        }
    }

    public void setLambda(double lambda1, double lambda2){
        if (N == 3){
            interpolated = true;
            this.lambda1 = lambda1;
            this.lambda2 = lambda2;
        }
    }

    public void calculateNGramProbabilities(ArrayList<ArrayList<Symbol>> corpus, TrainedSmoothing<Symbol> trainedSmoothing){
        trainedSmoothing.train(corpus, this);
    }

    public void calculateNGramProbabilities(SimpleSmoothing<Symbol> simpleSmoothing){
        simpleSmoothing.setProbabilities(this);
    }

    public void calculateNGramProbabilities(SimpleSmoothing<Symbol> simpleSmoothing, int level){
        simpleSmoothing.setProbabilities(this, level);
    }

    public void replaceUnknownWords(HashSet<Symbol> dictionary){
        rootNode.replaceUnknownWords(dictionary);
    }

    public HashSet<Symbol> constructDictionaryWithNonRareWords(int level, double probability){
        HashSet<Symbol> result = new HashSet<>();
        CounterHashMap<Symbol> wordCounter = new CounterHashMap<>();
        rootNode.countWords(wordCounter, level);
        int sum = wordCounter.sumOfCounts();
        for (Symbol symbol : wordCounter.keySet()){
            if (wordCounter.get(symbol) / (sum + 0.0) > probability){
                result.add(symbol);
            }
        }
        return result;
    }

    private double getUniGramPerplexity(ArrayList<ArrayList<Symbol>> corpus){
        double sum = 0;
        int count = 0;
        for (int i = 0; i < corpus.size(); i++){
            for (int j = 0; j < corpus.get(i).size(); j++){
                double p = getProbability(corpus.get(i).get(j));
                sum -= Math.log(p);
                count++;
            }
        }
        return Math.exp(sum / count);
    }

    private double getBiGramPerplexity(ArrayList<ArrayList<Symbol>> corpus){
        double sum = 0;
        int count = 0;
        for (int i = 0; i < corpus.size(); i++){
            for (int j = 0; j < corpus.get(i).size() - 1; j++){
                double p = getProbability(corpus.get(i).get(j), corpus.get(i).get(j + 1));
                if (p == 0){
                    System.out.println("Zero probability");
                }
                sum -= Math.log(p);
                count++;
            }
        }
        return Math.exp(sum / count);
    }

    private double getTriGramPerplexity(ArrayList<ArrayList<Symbol>> corpus){
        double sum = 0;
        int count = 0;
        for (int i = 0; i < corpus.size(); i++){
            for (int j = 0; j < corpus.get(i).size() - 2; j++){
                double p = getProbability(corpus.get(i).get(j), corpus.get(i).get(j + 1), corpus.get(i).get(j + 2));
                sum -= Math.log(p);
                count++;
            }
        }
        return Math.exp(sum / count);
    }

    public double getPerplexity(ArrayList<ArrayList<Symbol>> corpus){
        switch (N){
            case 1:
                return getUniGramPerplexity(corpus);
            case 2:
                return getBiGramPerplexity(corpus);
            case 3:
                return getTriGramPerplexity(corpus);
            default:
                return 0;
        }
    }

    public double getProbability(Symbol ... symbols) {
        switch (N){
            case 1:
                return getUniGramProbability(symbols[0]);
            case 2:
                if (interpolated){
                    return lambda1 * getBiGramProbability(symbols[0], symbols[1]) + (1 - lambda1) * getUniGramProbability(symbols[1]);
                } else {
                    return getBiGramProbability(symbols[0], symbols[1]);
                }
            case 3:
                if (interpolated){
                    return lambda1 * getTriGramProbability(symbols[0], symbols[1], symbols[2]) + lambda2 * getBiGramProbability(symbols[1], symbols[2]) + (1 - lambda1 - lambda2) * getUniGramProbability(symbols[2]);
                } else {
                    return getTriGramProbability(symbols[0], symbols[1], symbols[2]);
                }
        }
        return 0.0;
    }

    private double getUniGramProbability(Symbol w1) {
        return rootNode.getUniGramProbability(w1);
    }

    private double getBiGramProbability(Symbol w1, Symbol w2) {
        try {
            return rootNode.getBiGramProbability(w1, w2);
        } catch (UnseenCase unseenCase) {
            return probabilityOfUnseen[1];
        }
    }

    private double getTriGramProbability(Symbol w1, Symbol w2, Symbol w3) {
        try {
            return rootNode.getTriGramProbability(w1, w2, w3);
        } catch (UnseenCase unseenCase) {
            return probabilityOfUnseen[2];
        }
    }

    public int getCount(Symbol[] symbols){
        return rootNode.getCount(symbols, 0);
    }

    public void setProbabilityWithPseudoCount(double pseudoCount, int height){
        double vocabularySize;
        if (pseudoCount != 0){
            vocabularySize = vocabularySize() + 1;
        } else {
            vocabularySize = vocabularySize();
        }
        rootNode.setProbabilityWithPseudoCount(pseudoCount, height, vocabularySize);
        probabilityOfUnseen[height - 1] = 1.0 / vocabularySize;
    }

    private int maximumOccurrence(int height){
        return rootNode.maximumOccurrence(height);
    }

    private void updateCountsOfCounts(int[] countsOfCounts, int height){
        rootNode.updateCountsOfCounts(countsOfCounts, height);
    }

    public int[] calculateCountsOfCounts(int height){
        int maxCount;
        int[] countsOfCounts;
        maxCount = maximumOccurrence(height);
        countsOfCounts = new int[maxCount + 2];
        updateCountsOfCounts(countsOfCounts, height);
        return countsOfCounts;
    }

    public void setAdjustedProbability(double[] countsOfCounts, int height, double pZero){
        rootNode.setAdjustedProbability(countsOfCounts, height, vocabularySize() + 1, pZero);
        probabilityOfUnseen[height - 1] = 1.0 / (vocabularySize() + 1);
    }

    public void save(String fileName){
        FileOutputStream outFile;
        ObjectOutputStream outObject;
        try {
            outFile = new FileOutputStream(fileName);
            outObject = new ObjectOutputStream (outFile);
            outObject.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
