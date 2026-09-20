package SpellChecking.Trie;

import java.util.ArrayList;
import java.util.List;

class TrieNode {
    private final Character letter;
    private final List<TrieNode> children = new ArrayList<>();

    public TrieNode(char letter) {
        this.letter = letter;
    }

    public TrieNode() {
        this.letter = null;
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
}
