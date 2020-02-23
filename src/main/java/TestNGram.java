import Ngram.LaplaceSmoothing;
import Ngram.NGram;
import Ngram.NoSmoothing;

public class TestNGram {

    public static void main(String[] args){
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
}
