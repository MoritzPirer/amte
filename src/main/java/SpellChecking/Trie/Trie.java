package SpellChecking.Trie;

import SpellChecking.WordChecker;
import SpellChecking.WordCorrecter;

import java.util.Collection;

public class Trie implements WordChecker, WordCorrecter {
    private final TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    @Override
    public boolean contains(String word) {
        return root.contains(word);
    }

    @Override
    public String[] correct(String word, int maxResults) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void add(String word) {
        root.insert(word);
    }

    @Override
    public void addAll(Collection<String> words) {
        for (String word : words) {
            add(word);
        }
    }
}
