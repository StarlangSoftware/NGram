package Ngram;

import DataStructure.CounterHashMap;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

public class NGramNode<Symbol> implements Serializable {

    private HashMap<Symbol, NGramNode<Symbol>> children = null;
    private Symbol symbol;
    private int count;
    private double probability;
    private double probabilityOfUnseen;
    private NGramNode<Symbol> unknown = null;

    /**
     * Constructor of {@link NGramNode}
     *
     * @param symbol symbol to be kept in this node.
     */
    public NGramNode(Symbol symbol) {
        this.symbol = symbol;
        count = 0;
    }

    /**
     * Constructor of {@link NGramNode}
     *
     * @param isRootNode True if this node is root node, false otherwise.
     * @param br         File to be read.
     */
    public NGramNode(boolean isRootNode, BufferedReader br) {
        try {
            if (!isRootNode) {
                this.symbol = (Symbol) br.readLine().trim();
            }
            String line = br.readLine().trim();
            String[] items = line.split(" ");
            this.count = Integer.parseInt(items[0]);
            this.probability = Double.parseDouble(items[1]);
            this.probabilityOfUnseen = Double.parseDouble(items[2]);
            int numberOfChildren = Integer.parseInt(items[3]);
            if (numberOfChildren > 0){
                children = new HashMap<>();
                for (int i = 0; i < numberOfChildren; i++){
                    NGramNode<Symbol> childNode = new NGramNode<Symbol>(false, br);
                    children.put(childNode.symbol, childNode);
                }
            }
        } catch (IOException e) {
        }
    }

    /**
     * Constructor of {@link NGramNode}
     *
     * @param isRootNode True if this node is root node, false otherwise.
     * @param multipleFile Multiple file structure to read the nGram.
     */
    public NGramNode(boolean isRootNode, MultipleFile multipleFile) {
        if (!isRootNode) {
            this.symbol = (Symbol) multipleFile.readLine().trim();
        }
        String line = multipleFile.readLine().trim();
        String[] items = line.split(" ");
        this.count = Integer.parseInt(items[0]);
        this.probability = Double.parseDouble(items[1]);
        this.probabilityOfUnseen = Double.parseDouble(items[2]);
        int numberOfChildren = Integer.parseInt(items[3]);
        if (numberOfChildren > 0){
            children = new HashMap<>();
            for (int i = 0; i < numberOfChildren; i++){
                NGramNode<Symbol> childNode = new NGramNode<Symbol>(false, multipleFile);
                children.put(childNode.symbol, childNode);
            }
        }
    }

    /**
     * Gets count of this node.
     *
     * @return count of this node.
     */
    public int getCount() {
        return count;
    }

    /**
     * Gets the size of children of this node.
     *
     * @return size of children of {@link NGramNode} this node.
     */
    public int size() {
        return children.size();
    }

    /**
     * Finds maximum occurrence. If height is 0, returns the count of this node.
     * Otherwise, traverses this nodes' children recursively and returns maximum occurrence.
     *
     * @param height height for NGram.
     * @return maximum occurrence.
     */
    public int maximumOccurrence(int height) {
        int current, max = 0;
        if (height == 0) {
            return count;
        } else {
            for (NGramNode<Symbol> child : children.values()) {
                current = child.maximumOccurrence(height - 1);
                if (current > max) {
                    max = current;
                }
            }
            return max;
        }
    }

    /**
     * @return sum of counts of children nodes.
     */
    double childSum() {
        double sum = 0;
        for (NGramNode<Symbol> child : children.values()) {
            sum += child.count;
        }
        if (unknown != null) {
            sum += unknown.count;
        }
        return sum;
    }

    /**
     * Traverses nodes and updates counts of counts for each node.
     *
     * @param countsOfCounts counts of counts of NGrams.
     * @param height         height for NGram. if height = 1, If level = 1, N-Gram is treated as UniGram, if level = 2,
     *                       N-Gram is treated as Bigram, etc.
     */
    void updateCountsOfCounts(int[] countsOfCounts, int height) {
        if (height == 0) {
            countsOfCounts[count]++;
        } else {
            for (NGramNode<Symbol> child : children.values()) {
                child.updateCountsOfCounts(countsOfCounts, height - 1);
            }
        }
    }

    /**
     * Sets probabilities by traversing nodes and adding pseudocount for each NGram.
     *
     * @param pseudoCount    pseudocount added to each NGram.
     * @param height         height for NGram. if height = 1, If level = 1, N-Gram is treated as UniGram, if level = 2,
     *                       N-Gram is treated as Bigram, etc.
     * @param vocabularySize size of vocabulary
     */
    void setProbabilityWithPseudoCount(double pseudoCount, int height, double vocabularySize) {
        if (height == 1) {
            double sum = childSum() + pseudoCount * vocabularySize;
            for (NGramNode<Symbol> child : children.values()) {
                child.probability = (child.count + pseudoCount) / sum;
            }
            if (unknown != null) {
                unknown.probability = (unknown.count + pseudoCount) / sum;
            }
            probabilityOfUnseen = pseudoCount / sum;
        } else {
            for (NGramNode<Symbol> child : children.values()) {
                child.setProbabilityWithPseudoCount(pseudoCount, height - 1, vocabularySize);
            }
        }
    }

    /**
     * Sets adjusted probabilities with counts of counts of NGrams.
     * For count < 5, count is considered as ((r + 1) * N[r + 1]) / N[r]), otherwise, count is considered as it is.
     * Sum of children counts are computed. Then, probability of a child node is (1 - pZero) * (r / sum) if r > 5
     * otherwise, r is replaced with ((r + 1) * N[r + 1]) / N[r]) and calculated the same.
     *
     * @param N              counts of counts of NGrams.
     * @param height         height for NGram. if height = 1, If level = 1, N-Gram is treated as UniGram, if level = 2,
     *                       N-Gram is treated as Bigram, etc.
     * @param vocabularySize size of vocabulary.
     * @param pZero          probability of zero.
     */
    void setAdjustedProbability(double[] N, int height, double vocabularySize, double pZero) {
        if (height == 1) {
            double sum = 0;
            for (NGramNode<Symbol> child : children.values()) {
                int r = child.count;
                if (r <= 5) {
                    double newR = ((r + 1) * N[r + 1]) / N[r];
                    sum += newR;
                } else {
                    sum += r;
                }
            }
            for (NGramNode<Symbol> child : children.values()) {
                int r = child.count;
                if (r <= 5) {
                    double newR = ((r + 1) * N[r + 1]) / N[r];
                    child.probability = (1 - pZero) * (newR / sum);
                } else {
                    child.probability = (1 - pZero) * (r / sum);
                }
            }
            probabilityOfUnseen = pZero / (vocabularySize - children.size());
        } else {
            for (NGramNode<Symbol> child : children.values()) {
                child.setAdjustedProbability(N, height - 1, vocabularySize, pZero);
            }
        }
    }

    /**
     * Adds NGram given as array of symbols to the node as a child.
     *
     * @param s      array of symbols
     * @param index  start index of NGram
     * @param height height for NGram. if height = 1, If level = 1, N-Gram is treated as UniGram, if level = 2,
     *               N-Gram is treated as Bigram, etc.
     */
    void addNGram(Symbol[] s, int index, int height) {
        addNGram(s, index, height, 1);
    }

    /**
     * Adds count times NGram given as array of symbols to the node as a child.
     *
     * @param s      array of symbols
     * @param index  start index of NGram
     * @param height height for NGram. if height = 1, If level = 1, N-Gram is treated as UniGram, if level = 2,
     *               N-Gram is treated as Bigram, etc.
     * @param count  Number of times this NGram is added.
     */
    void addNGram(Symbol[] s, int index, int height, int count) {
        NGramNode<Symbol> child;
        if (height == 0) {
            return;
        }
        Symbol symbol = s[index];
        if (children != null && children.containsKey(symbol)) {
            //System.out.println("contains " + symbol.toString());
            child = children.get(symbol);
        } else {
            child = new NGramNode<>(symbol);
            if (children == null) {
                children = new HashMap<>();
            }
            children.put(symbol, child);
        }
        child.count += count;
        child.addNGram(s, index + 1, height - 1, count);
    }

    /**
     * Gets unigram probability of given symbol.
     *
     * @param w1 unigram.
     * @return unigram probability of given symbol.
     */
    double getUniGramProbability(Symbol w1) {
        if (children.containsKey(w1)) {
            return children.get(w1).probability;
        } else {
            if (unknown != null) {
                return unknown.probability;
            }
            return probabilityOfUnseen;
        }
    }

    /**
     * Gets bigram probability of given symbols w1 and w2
     *
     * @param w1 first gram of bigram.
     * @param w2 second gram of bigram.
     * @return probability of given bigram
     * @throws UnseenCase When the first symbol is seen but not the second
     */
    double getBiGramProbability(Symbol w1, Symbol w2) throws UnseenCase {
        NGramNode<Symbol> child;
        if (children.containsKey(w1)) {
            child = children.get(w1);
            return child.getUniGramProbability(w2);
        } else {
            if (unknown != null) {
                return unknown.getUniGramProbability(w2);
            }
            throw new UnseenCase();
        }
    }

    /**
     * Gets trigram probability of given symbols w1, w2 and w3.
     *
     * @param w1 first gram of trigram
     * @param w2 second gram of trigram
     * @param w3 third gram of trigram
     * @return probability of given trigram.
     * @throws UnseenCase When the first symbol is seen but not the second or third
     */
    double getTriGramProbability(Symbol w1, Symbol w2, Symbol w3) throws UnseenCase {
        NGramNode<Symbol> child;
        if (children.containsKey(w1)) {
            child = children.get(w1);
            return child.getBiGramProbability(w2, w3);
        } else {
            if (unknown != null) {
                return unknown.getBiGramProbability(w2, w3);
            }
            throw new UnseenCase();
        }
    }

    /**
     * Counts words recursively given height and wordCounter.
     *
     * @param wordCounter word counter keeping symbols and their counts.
     * @param height      height for NGram. if height = 1, If level = 1, N-Gram is treated as UniGram, if level = 2,
     *                    N-Gram is treated as Bigram, etc.
     */
    void countWords(CounterHashMap<Symbol> wordCounter, int height) {
        if (height == 0) {
            wordCounter.putNTimes(symbol, count);
        } else {
            for (NGramNode<Symbol> child : children.values()) {
                child.countWords(wordCounter, height - 1);
            }
        }
    }

    /**
     * Replace words not in given dictionary.
     * Deletes unknown words from children nodes and adds them to {@link NGramNode#unknown} unknown node as children recursively.
     *
     * @param dictionary dictionary of known words.
     */
    void replaceUnknownWords(HashSet<Symbol> dictionary) {
        if (children != null) {
            ArrayList<NGramNode<Symbol>> childList = new ArrayList<>();
            for (Symbol symbol : children.keySet()) {
                if (!dictionary.contains(symbol)) {
                    childList.add(children.get(symbol));
                }
            }
            if (childList.size() > 0) {
                unknown = new NGramNode<>(null);
                unknown.children = new HashMap<>();
                int sum = 0;
                for (NGramNode<Symbol> child : childList) {
                    if (child.children != null) {
                        unknown.children.putAll(child.children);
                    }
                    sum += child.count;
                    children.remove(child.symbol);
                }
                unknown.count = sum;
                unknown.replaceUnknownWords(dictionary);
            }
            for (NGramNode<Symbol> child : children.values()) {
                child.replaceUnknownWords(dictionary);
            }
        }
    }

    /**
     * Gets count of symbol given array of symbols and index of symbol in this array.
     *
     * @param s     array of symbols
     * @param index index of symbol whose count is returned
     * @return count of the symbol.
     */
    int getCount(Symbol[] s, int index) {
        if (index < s.length) {
            if (children.containsKey(s[index])) {
                return children.get(s[index]).getCount(s, index + 1);
            } else {
                return 0;
            }
        } else {
            return getCount();
        }
    }

    /**
     * Generates next string for given list of symbol and index
     *
     * @param s     list of symbol
     * @param index index index of generated string
     * @return generated string.
     */
    public Symbol generateNextString(ArrayList<Symbol> s, int index) {
        double sum = 0.0, prob;
        if (index == s.size()) {
            prob = Math.random();
            for (NGramNode<Symbol> node : children.values()) {
                if (prob < node.probability + sum) {
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

    public void prune(double threshold, int N){
        if (N == 0){
            ArrayList<Symbol> toBeDeleted = new ArrayList<>();
            for (Symbol symbol : children.keySet()) {
                if (children.get(symbol).count / (count + 0.0) < threshold){
                    toBeDeleted.add(symbol);
                }
            }
            for (Symbol symbol : toBeDeleted){
                children.remove(symbol);
            }
        } else {
            for (NGramNode<Symbol> node : children.values()) {
                node.prune(threshold, N - 1);
            }
        }
    }

    /**
     * Save this NGramNode to a text file.
     *
     * @param isRootNode True if this not is a root node, false otherwise
     * @param fw {@link BufferedWriter} file where NGram is saved.
     * @param level Level of this node.
     */
    public void saveAsText(boolean isRootNode, BufferedWriter fw, int level) {
        try {
            if (!isRootNode) {
                for (int i = 0; i < level; i++) {
                    fw.write("\t");
                }
                fw.write(symbol.toString() + "\n");
            }
            for (int i = 0; i < level; i++) {
                fw.write("\t");
            }
            if (children != null){
                fw.write(count + " " + probability + " " + probabilityOfUnseen + " " + size() + "\n");
                for (NGramNode<Symbol> child : children.values()) {
                    child.saveAsText(false, fw, level + 1);
                }
            } else {
                fw.write(count + " " + probability + " " + probabilityOfUnseen + " 0\n");
            }
        } catch (IOException e) {
        }
    }
}
