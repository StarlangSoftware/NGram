package Ngram;

import DataStructure.CounterHashMap;
import Dictionary.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class NGramNode<Symbol> implements Serializable{

    private HashMap<Symbol, NGramNode<Symbol>> children = null;
    private Symbol symbol;
    private int count;
    private double probability;
    private double probabilityOfUnseen;
    private NGramNode<Symbol> unknown = null;

    public NGramNode(Symbol symbol) {
        this.symbol = symbol;
        count = 0;
    }

    public int getCount(){
        return count;
    }

    public int size() {
        return children.size();
    }

    public int maximumOccurrence(int height){
        int current, max = 0;
        if (height == 0){
            return count;
        } else {
            for (NGramNode child : children.values()){
                current = child.maximumOccurrence(height - 1);
                if (current > max){
                    max = current;
                }
            }
            return max;
        }
    }

    double childSum(){
        double sum = 0;
        for (NGramNode child : children.values()){
            sum += child.count;
        }
        if (unknown != null){
            sum += unknown.count;
        }
        return sum;
    }

    void updateCountsOfCounts(int[] countsOfCounts, int height){
        if (height == 0){
            countsOfCounts[count]++;
        } else {
            for (NGramNode child : children.values()){
                child.updateCountsOfCounts(countsOfCounts, height - 1);
            }
        }
    }

    void setProbabilityWithPseudoCount(double pseudoCount, int height, double vocabularySize){
        if (height == 1){
            double sum = childSum() + pseudoCount * vocabularySize;
            for (NGramNode child : children.values()){
                child.probability = (child.count + pseudoCount) / sum;
            }
            if (unknown != null){
                unknown.probability = (unknown.count + pseudoCount) / sum;
            }
            probabilityOfUnseen = pseudoCount / sum;
        } else {
            for (NGramNode child : children.values()) {
                child.setProbabilityWithPseudoCount(pseudoCount, height - 1, vocabularySize);
            }
        }
    }

    void setAdjustedProbability(double[] N, int height, double vocabularySize, double pZero){
        if (height == 1){
            double sum = 0;
            for (NGramNode child : children.values()){
                int r = child.count;
                if (r <= 5){
                    double newR = ((r + 1) * N[r + 1]) / N[r];
                    sum += newR;
                } else {
                    sum += r;
                }
            }
            for (NGramNode child : children.values()){
                int r = child.count;
                if (r <= 5){
                    double newR = ((r + 1) * N[r + 1]) / N[r];
                    child.probability = (1 - pZero) * (newR / sum);
                } else {
                    child.probability = (1 - pZero) * (r / sum);
                }
            }
            probabilityOfUnseen = pZero / (vocabularySize - children.size());
        } else {
            for (NGramNode child : children.values()){
                child.setAdjustedProbability(N, height - 1, vocabularySize, pZero);
            }
        }
    }

    void addNGram(Symbol[] s, int index, int height){
        NGramNode<Symbol> child;
        if (height == 0){
            return;
        }
        Symbol symbol = s[index];
        if (children != null && children.containsKey(symbol)){
            child = children.get(symbol);
        } else {
            child = new NGramNode<Symbol>(symbol);
            if (children == null){
                children = new HashMap<>();
            }
            children.put(symbol, child);
        }
        child.count++;
        child.addNGram(s, index + 1, height - 1);
    }

    double getUniGramProbability(Symbol w1) {
        if (children.containsKey(w1)){
            return children.get(w1).probability;
        } else {
            if (unknown != null){
                return unknown.probability;
            }
            return probabilityOfUnseen;
        }
    }

    double getBiGramProbability(Symbol w1, Symbol w2) throws UnseenCase {
        NGramNode<Symbol> child;
        if (children.containsKey(w1)){
            child = children.get(w1);
            return child.getUniGramProbability(w2);
        } else {
            if (unknown != null){
                return unknown.getUniGramProbability(w2);
            }
            throw new UnseenCase();
        }
    }

    double getTriGramProbability(Symbol w1, Symbol w2, Symbol w3) throws UnseenCase {
        NGramNode<Symbol> child;
        if (children.containsKey(w1)){
            child = children.get(w1);
            return child.getBiGramProbability(w2, w3);
        } else {
            if (unknown != null){
                return unknown.getBiGramProbability(w2, w3);
            }
            throw new UnseenCase();
        }
    }

    void countWords(CounterHashMap<Symbol> wordCounter, int height){
        if (height == 0){
            wordCounter.putNTimes(symbol, count);
        } else {
            for (NGramNode child : children.values()){
                child.countWords(wordCounter, height - 1);
            }
        }
    }

    void replaceUnknownWords(HashSet<Symbol> dictionary){
        if (children != null){
            ArrayList<NGramNode<Symbol>> childList = new ArrayList<>();
            for (Symbol symbol : children.keySet()){
                if (!dictionary.contains(symbol)){
                    childList.add(children.get(symbol));
                }
            }
            if (childList.size() > 0){
                unknown = new NGramNode<Symbol>(null);
                unknown.children = new HashMap<>();
                int sum = 0;
                for (NGramNode<Symbol> child : childList){
                    if (child.children != null){
                        unknown.children.putAll(child.children);
                    }
                    sum += child.count;
                    children.remove(child.symbol);
                }
                unknown.count = sum;
                unknown.replaceUnknownWords(dictionary);
            }
            for (NGramNode<Symbol> child : children.values()){
                child.replaceUnknownWords(dictionary);
            }
        }
    }

    int getCount(Symbol[] s, int index){
        if (index < s.length){
            if (children.containsKey(s[index])){
                return children.get(s[index]).getCount(s, index + 1);
            } else {
                return 0;
            }
        } else {
            return getCount();
        }
    }

    public Symbol generateNextString(ArrayList<Symbol> s, int index){
        double sum = 0.0, prob;
        if (index == s.size()){
            prob = Math.random();
            for (NGramNode<Symbol> node : children.values()){
                if (prob < node.probability + sum){
                    return node.symbol;
                } else {
                    sum += node.probability;
                }
            }
        } else {
            return children.get(s.get(index)).generateNextString(s, index + 1);
        }
        return null;
    }
}
