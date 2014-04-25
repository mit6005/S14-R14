package staff;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A WordSet is an ordered set of words that is searchable by substring.
 * A word is defined as a sequence of letters (upper case or lower case) and
 * apostrophes (to allow contractions like "don't"). WordSets ignore alphabetic
 * case when searching and sorting.
 */
public class WordSet {
    
    private final List<String> words = new ArrayList<String>();
    
    /*
     * Rep invariant: words != null
     */
    
    /**
     * Make an empty WordList.
     */
    public WordSet() { }
    
    /**
     * Load a stream into this word set.
     * Removes all the words from this word set and replaces them with the words
     * found in the stream (treating punctuation, whitespace, and numbers as
     * delimiters between words).
     * 
     * @param in an open stream consisting of a sequence of words
     * @throws IOException if an error occurred while reading the stream
     */
    public void load(InputStream in) throws IOException {
        Collator c = Collator.getInstance();
        c.setStrength(Collator.PRIMARY);
        Set<String> set = new TreeSet<>(c);
        
        StreamTokenizer tok = new StreamTokenizer(new InputStreamReader(in));
        tok.resetSyntax();
        tok.wordChars('a', 'z');
        tok.wordChars('A', 'Z');
        tok.wordChars('\'', '\'');
        
        while (tok.nextToken() != StreamTokenizer.TT_EOF) {
            if (tok.ttype == StreamTokenizer.TT_WORD)
                set.add(tok.sval);
        }
        
        words.clear();
        words.addAll(set);
    }
    
    /**
     * Find words containing a given substring.
     * 
     * @param sub non-null substring to search for
     * @return list of words in this word set (sorted case-insensitively) that
     *         contain the substring sub (matched case-insensitively). A word
     *         appears at most once in the returned list.
     */
    public List<String> find(String sub) {
        if (sub.length() == 0) {
            return Collections.unmodifiableList(words);
        }
        
        sub = sub.toLowerCase();
        List<String> matches = new ArrayList<>();
        for (String word : words) {
            if (word.toLowerCase().indexOf(sub) != -1) {
                matches.add(word);
                
                /*
                 * What if finding takes a long time proportional to # matches?
                 */
                // try { Thread.sleep(5); } catch (InterruptedException ie) { }
            }
        }
        return matches;
    }
    
    /**
     * Main method, demonstrates how to use this class.
     * @param args command-line arguments, ignored
     */
    public static void main(String[] args) throws IOException {
        WordSet words = new WordSet ();
        
        InputStream in = new FileInputStream("words");
        words.load(in);
        
        // print all the words containing "ph"
        List<String> matches = words.find("ph");
        for (String match : matches) {
            System.out.println(match);
        }
    }
}
