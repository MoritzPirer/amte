package SpellChecking;

import java.util.ArrayList;
import java.util.List;

class TrieNode {
    final Character letter;
    final List<TrieNode> children = new ArrayList<>();

    public TrieNode(char letter) {
        this.letter = letter;
    }

    /**
     * null is used by the root node, which doesn't have a letter, and as a sentinel value to designate
     * the end of a word (to prevent an invalid word being matched because it is the prefix of a valid word)
     */
    public TrieNode() {
        this.letter = null; // null
    }

    public boolean contains(String word) {
        if (word.isEmpty()) {
            return getChild(null) != null;
        }

        TrieNode next = getChild(word.charAt(0));
        if (next == null) {
            return false;
        }

        return next.contains(word.substring(1));
    }

    private TrieNode getChild(Character letter) {
        for (TrieNode child : children) {
            if (child.letter == letter) {
                return child;
            }
        }

        return null;
    }

    public void insert(String word) {
        if (word.isEmpty()) {
            children.add(new TrieNode());
            return;
        }

        TrieNode next = getChild(word.charAt(0));
        if (next == null) {
            next = new TrieNode(word.charAt(0));
            children.add(next);
        }

        next.insert(word.substring(1));
    }

    public TrieNode lastMatch(String word) {
        if (word.isEmpty()) {
            return this;
        }

        TrieNode next = getChild(word.charAt(0));
        if (next == null) {
            return this;
        }

        return next.lastMatch(word.substring(1));
    }

    public String getMatchedPrefix(String word) {
        return getMatchingPrefix(word, 0);
    }
    private String getMatchingPrefix(String word, int index) {
        if (index == word.length()) {
            return word;
        }

        TrieNode next = getChild(word.charAt(index));
        if (next == null) {
            return word.substring(0, index);
        }

        return next.getMatchingPrefix(word, index + 1);
    }
}
