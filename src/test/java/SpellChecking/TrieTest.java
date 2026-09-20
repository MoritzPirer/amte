package SpellChecking;

import SpellChecking.Trie.Trie;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrieTest {
    // prerequisite - action - result

    @Test
    public void givenEmptyTrie_whenInsertingWord_thenTrieContainsWord() {
        Trie trie = new Trie();
        trie.add("banana");

        assertTrue(trie.contains("banana"));
    }

    @Test
    public void givenEmptyTrie_whenInsertingMultiple_thenAllAreContained() {
        Trie trie = new Trie();

        String[] words = new String[]{
            "apple",
            "banana",
            "cherry"
        };

        for (String word : words) {
            trie.add(word);
        }

        for (String word : words) {
            assertTrue(trie.contains(word));
        }
    }

    @Test
    public void givenEmptyTrie_whenBulkInserting_thenAllAreContained() {
        Trie trie = new Trie();

        List<String> words = List.of("apple", "banana", "cherry");

        trie.addAll(words);

        for (String word : words) {
            assertTrue(trie.contains(word));
        }
    }

    @Test
    public void givenTrie_whenWordWasNotInserted_thenWordIsNotContained() {
        Trie trie = new Trie();
        trie.add("banana");

        assertFalse(trie.contains("apple"));
    }

    @Test
    public void givenTry_whenWordIsInsertedTwice_thenSecondInsertionIsIgnored() {
        Trie trie = new Trie();
        trie.add("banana");
        trie.add("banana");

        assertTrue(trie.contains("banana"));
    }

    @Test
    public void givenTrie_whenWordIsInserted_thenPrefixIsNotContained() {
        Trie trie = new Trie();
        trie.add("banana");
        assertFalse(trie.contains("ban"));
    }
}
