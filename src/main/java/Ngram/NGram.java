package Ngram;

import DataStructure.CounterHashMap;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class NGram<Symbol> implements Serializable{


    public NGramNode<Symbol> rootNode;
    private int N;
    private double lambda1, lambda2;
    private boolean interpolated = false;
    private HashSet<Symbol> vocabulary;
    private double[] probabilityOfUnseen;

    /**
     * Constructor of {@link NGram} class which takes a {@link ArrayList} corpus and {@link Integer} size of ngram as input.
     * It adds all sentences of corpus as ngrams.
     *
     * @param corpus {@link ArrayList} list of sentences whose ngrams are added.
     * @param N size of ngram.
     */
    public NGram(ArrayList<ArrayList<Symbol>> corpus, int N){
        int i;
        this.N = N;
        this.vocabulary = new HashSet<>();
        probabilityOfUnseen = new double[N];
        rootNode = new NGramNode<Symbol>(null);
        for (i = 0; i < corpus.size(); i++)
            addNGramSentence((Symbol[]) corpus.get(i).toArray());
    }

    /**
     * Constructor of {@link NGram} class which takes {@link Integer} size of ngram.
     *
     * @param N size of ngram.
     */
    public NGram(int N){
        this.N = N;
        this.vocabulary = new HashSet<>();
        this.probabilityOfUnseen = new double[N];
        rootNode = new NGramNode<>(null);
    }

    /**
     * Constructor of {@link NGram} class which takes filename to read from text file.
     *
     * @param fileName name of the text file where NGram is saved.
     */
    public NGram(String fileName){
        String line;
        int vocabularySize;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8));
            line = br.readLine();
            String[] items = line.split(" ");
            this.N = Integer.parseInt(items[0]);
            this.lambda1 = Double.parseDouble(items[1]);
            this.lambda2 = Double.parseDouble(items[2]);
            this.probabilityOfUnseen = new double[N];
            for (int i = 0; i < N; i++){
                this.probabilityOfUnseen[i] = Double.parseDouble(br.readLine());
            }
            this.vocabulary = new HashSet<>();
            vocabularySize = Integer.parseInt(br.readLine());
            for (int i = 0; i < vocabularySize; i++){
                this.vocabulary.add((Symbol) br.readLine());
            }
            rootNode = new NGramNode<>(true, br);
            br.close();
        } catch (IOException e) {
        }
    }

    /**
     *
     * @return size of ngram.
     */
    public int getN(){
        return N;
    }

    /**
     * Set size of ngram.
     * @param N size of ngram
     */
    public void setN(int N){
        this.N = N;
    }

    /**
     * Adds {@link Symbol[]} given array of symbols to {@link HashSet} the vocabulary and to {@link NGramNode} the rootNode
     *
     * @param symbols {@link Symbol[]} ngram added.
     */
    public void addNGram(Symbol[] symbols){
        vocabulary.addAll(Arrays.asList(symbols));
        rootNode.addNGram(symbols, 0, N);
    }

    /**
     * Adds given sentence to {@link HashSet} the vocabulary and create and add ngrams of the sentence to {@link NGramNode} the rootNode
     *
     * @param symbols {@link Symbol[]} sentence whose ngrams are added.
     */
    public void addNGramSentence(Symbol[] symbols){
        vocabulary.addAll(Arrays.asList(symbols));
        for (int j = 0; j < symbols.length - N + 1; j++){
            rootNode.addNGram(symbols, j, N);
        }
    }

    /**
     * @return vocabulary size.
     */
    public double vocabularySize(){
        return vocabulary.size();
    }

    /**
     * Sets lambda, interpolation ratio, for bigram and unigram probabilities.
     *
     * ie. lambda1 * bigramProbability + (1 - lambda1) * unigramProbability
     *
     * @param lambda1 interpolation ratio for bigram probabilities
     */
    public void setLambda(double lambda1){
        if (N == 2){
            interpolated = true;
            this.lambda1 = lambda1;
        }
    }

    /**
     * Sets lambdas, interpolation ratios, for trigram, bigram and unigram probabilities.
     * ie. lambda1 * trigramProbability + lambda2 * bigramProbability  + (1 - lambda1 - lambda2) * unigramProbability
     *
     * @param lambda1 interpolation ratio for trigram probabilities
     * @param lambda2 interpolation ratio for bigram probabilities
     */
    public void setLambda(double lambda1, double lambda2){
        if (N == 3){
            interpolated = true;
            this.lambda1 = lambda1;
            this.lambda2 = lambda2;
        }
    }

    /**
     * Calculates NGram probabilities using {@link ArrayList} given corpus and {@link TrainedSmoothing<Symbol>} smoothing method.
     *
     * @param corpus corpus for calculating NGram probabilities.
     * @param trainedSmoothing instance of smoothing method for calculating ngram probabilities.
     */
    public void calculateNGramProbabilities(ArrayList<ArrayList<Symbol>> corpus, TrainedSmoothing<Symbol> trainedSmoothing){
        trainedSmoothing.train(corpus, this);
    }

    /**
     * Calculates NGram probabilities using {@link SimpleSmoothing} simple smoothing.
     *
     * @param simpleSmoothing {@link SimpleSmoothing}
     */
    public void calculateNGramProbabilities(SimpleSmoothing<Symbol> simpleSmoothing){
        simpleSmoothing.setProbabilities(this);
    }

    /**
     * Calculates NGram probabilities given {@link SimpleSmoothing} simple smoothing and level.
     *
     * @param simpleSmoothing {@link SimpleSmoothing}
     * @param level Level for which N-Gram probabilities will be set.
     *
     */
    public void calculateNGramProbabilities(SimpleSmoothing<Symbol> simpleSmoothing, int level){
        simpleSmoothing.setProbabilities(this, level);
    }

    /**
     * Replaces words not in {@link HashSet} given dictionary.
     *
     * @param dictionary dictionary of known words.
     */
    public void replaceUnknownWords(HashSet<Symbol> dictionary){
        rootNode.replaceUnknownWords(dictionary);
    }


    /**
     * Constructs a dictionary of nonrare words with given N-Gram level and probability threshold.
     *
     * @param level Level for counting words. Counts for different levels of the N-Gram can be set. If level = 1, N-Gram is treated as UniGram, if level = 2,
     *              N-Gram is treated as Bigram, etc.
     * @param probability probability threshold for nonrare words.
     * @return {@link HashSet} nonrare words.
     */
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

    /**
     * Calculates unigram perplexity of given corpus. First sums negative log likelihoods of all unigrams in corpus.
     * Then returns exp of average negative log likelihood.
     *
     * @param corpus corpus whose unigram perplexity is calculated.
     *
     * @return unigram perplexity of corpus.
     */
    private double getUniGramPerplexity(ArrayList<ArrayList<Symbol>> corpus){
        double sum = 0;
        int count = 0;
        for (ArrayList<Symbol> symbols : corpus) {
            for (Symbol symbol : symbols) {
                double p = getProbability(symbol);
                sum -= Math.log(p);
                count++;
            }
        }
        return Math.exp(sum / count);
    }

    /**
     * Calculates bigram perplexity of given corpus. First sums negative log likelihoods of all bigrams in corpus.
     * Then returns exp of average negative log likelihood.
     *
     * @param corpus corpus whose bigram perplexity is calculated.
     *
     * @return bigram perplexity of given corpus.
     */
    private double getBiGramPerplexity(ArrayList<ArrayList<Symbol>> corpus){
        double sum = 0;
        int count = 0;
        for (ArrayList<Symbol> symbols : corpus) {
            for (int j = 0; j < symbols.size() - 1; j++) {
                double p = getProbability(symbols.get(j), symbols.get(j + 1));
                if (p == 0) {
                    System.out.println("Zero probability");
                }
                sum -= Math.log(p);
                count++;
            }
        }
        return Math.exp(sum / count);
    }

    /**
     * Calculates trigram perplexity of given corpus. First sums negative log likelihoods of all trigrams in corpus.
     * Then returns exp of average negative log likelihood.
     *
     * @param corpus corpus whose trigram perplexity is calculated.
     * @return trigram perplexity of given corpus.
     */
    private double getTriGramPerplexity(ArrayList<ArrayList<Symbol>> corpus){
        double sum = 0;
        int count = 0;
        for (ArrayList<Symbol> symbols : corpus) {
            for (int j = 0; j < symbols.size() - 2; j++) {
                double p = getProbability(symbols.get(j), symbols.get(j + 1), symbols.get(j + 2));
                sum -= Math.log(p);
                count++;
            }
        }
        return Math.exp(sum / count);
    }

    /**
     * Calculates the perplexity of given corpus depending on N-Gram model (unigram, bigram, trigram, etc.)
     *
     * @param corpus corpus whose perplexity is calculated.
     * @return perplexity of given corpus
     */
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

    /**
     * Gets probability of sequence of symbols depending on N in N-Gram. If N is 1, returns unigram probability.
     * If N is 2, if interpolated is true, then returns interpolated bigram and unigram probability, otherwise returns only bigram probability.
     * If N is 3, if interpolated is true, then returns interpolated trigram, bigram and unigram probability, otherwise returns only trigram probability.
     * @param symbols sequence of symbol.
     * @return probability of given sequence.
     */
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

    /**
     * Gets unigram probability of given symbol.
     * @param w1 a unigram symbol.
     * @return probability of given unigram.
     */
    private double getUniGramProbability(Symbol w1) {
        return rootNode.getUniGramProbability(w1);
    }

    /**
     * Gets bigram probability of given symbols.
     * @param w1 first gram of bigram
     * @param w2 second gram of bigram
     * @return probability of bigram formed by w1 and w2.
     */
    private double getBiGramProbability(Symbol w1, Symbol w2) {
        try {
            return rootNode.getBiGramProbability(w1, w2);
        } catch (UnseenCase unseenCase) {
            return probabilityOfUnseen[1];
        }
    }

    /**
     * Gets trigram probability of given symbols.
     * @param w1 first gram of trigram
     * @param w2 second gram of trigram
     * @param w3 third gram of trigram
     * @return probability of trigram formed by w1, w2, w3.
     */
    private double getTriGramProbability(Symbol w1, Symbol w2, Symbol w3) {
        try {
            return rootNode.getTriGramProbability(w1, w2, w3);
        } catch (UnseenCase unseenCase) {
            return probabilityOfUnseen[2];
        }
    }

    /**
     * Gets count of given sequence of symbol.
     * @param symbols sequence of symbol.
     * @return count of symbols.
     */
    public int getCount(Symbol[] symbols){
        return rootNode.getCount(symbols, 0);
    }

    /**
     * Sets probabilities by adding pseudocounts given height and pseudocount.
     * @param pseudoCount pseudocount added to all N-Grams.
     * @param height  height for N-Gram. If height= 1, N-Gram is treated as UniGram, if height = 2,
     *                N-Gram is treated as Bigram, etc.
     */
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

    /**
     * Find maximum occurrence in given height.
     * @param height height for occurrences. If height = 1, N-Gram is treated as UniGram, if height = 2,
     *               N-Gram is treated as Bigram, etc.
     * @return maximum occurrence in given height.
     */
    private int maximumOccurrence(int height){
        return rootNode.maximumOccurrence(height);
    }

    /**
     * Update counts of counts of N-Grams with given counts of counts and given height.
     * @param countsOfCounts updated counts of counts.
     * @param height  height for NGram. If height = 1, N-Gram is treated as UniGram, if height = 2,
     *                N-Gram is treated as Bigram, etc.
     */
    private void updateCountsOfCounts(int[] countsOfCounts, int height){
        rootNode.updateCountsOfCounts(countsOfCounts, height);
    }

    /**
     * Calculates counts of counts of NGrams.
     * @param height  height for NGram. If height = 1, N-Gram is treated as UniGram, if height = 2,
     *                N-Gram is treated as Bigram, etc.
     * @return counts of counts of NGrams.
     */
    public int[] calculateCountsOfCounts(int height){
        int maxCount;
        int[] countsOfCounts;
        maxCount = maximumOccurrence(height);
        countsOfCounts = new int[maxCount + 2];
        updateCountsOfCounts(countsOfCounts, height);
        return countsOfCounts;
    }

    /**
     * Sets probability with given counts of counts and pZero.
     * @param countsOfCounts counts of counts of NGrams.
     * @param height  height for NGram. If height = 1, N-Gram is treated as UniGram, if height = 2,
     *                N-Gram is treated as Bigram, etc.
     * @param pZero probability of zero.
     */
    public void setAdjustedProbability(double[] countsOfCounts, int height, double pZero){
        rootNode.setAdjustedProbability(countsOfCounts, height, vocabularySize() + 1, pZero);
        probabilityOfUnseen[height - 1] = 1.0 / (vocabularySize() + 1);
    }

    /**
     * Save this NGram to a text file.
     *
     * @param fileName {@link String} name of file where NGram is saved.
     */
    public void saveAsText(String fileName){
        BufferedWriter fw;
        try {
            fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8));
            fw.write(N + " " + lambda1 + " " + lambda2 + "\n");
            for (int i = 0; i < N; i++){
                fw.write(probabilityOfUnseen[i] + " ");
            }
            fw.write("\n");
            fw.write(vocabularySize() + "\n");
            for (Symbol symbol : vocabulary){
                fw.write(symbol.toString() + "\n");
            }
            rootNode.saveAsText(true, fw, 0);
            fw.close();
        } catch (IOException e) {
        }
    }

    /**
     * Save this NGram to a file.
     *
     * @param fileName {@link String} name of file where NGram is saved.
     */
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
