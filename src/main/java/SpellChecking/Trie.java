package SpellChecking;

import java.util.*;

class Trie implements WordChecker, WordCorrecter, WordAddable {
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

        SortedSet<String> corrections = testDeletions(word);
        if (corrections.size() >= maxResults) {
            return convertToResult(corrections, maxResults);
        }

        corrections.addAll(testSwaps(word));
        if (corrections.size() >= maxResults) {
            return convertToResult(corrections, maxResults);
        }

        corrections.addAll(testInsertions(word));
        if (corrections.size() >= maxResults) {
            return convertToResult(corrections, maxResults);
        }

        corrections.addAll(testReplacements(word));

        return convertToResult(corrections, maxResults);
    }

    /**
     * convert from a Set to an Array, dropping leftover elements
     * @param words the corrections that were found
     * @param maxResults the maximum number to keep
     * @return an array with the first maxResults elements of words
     */
    private String[] convertToResult(SortedSet<String> words, int maxResults) {
        String[] rawResult = words.toArray(String[]::new);
        return Arrays.copyOfRange(rawResult, 0, Math.min(maxResults, rawResult.length));
    }

    /**
     * finds all valid words that can be achieved by deleting a single letter from word
     * @param word the word to correct
     * @return a set of valid words
     */
    private SortedSet<String> testDeletions(String word) {
        SortedSet<String> result = new TreeSet<>();

        for (int deletedIndex = 0; deletedIndex < word.length() - 1; deletedIndex++) {
            String current = word.substring(0, deletedIndex) + word.substring(deletedIndex + 1);
            if (contains(current)) {
                result.add(current);
            }
        }

        return result;
    }

    /**
     * finds all valid words that can be achieved by swapping two adjacent letters in word
     * @param word the word to correct
     * @return a set of valid words
     */
    private SortedSet<String> testSwaps(String word) {
        SortedSet<String> result = new TreeSet<>();

        if (word.length() < 2) {
            return result; //cannot swap with only one letter.
        }

        for (int swapIndex = 0; swapIndex < word.length() - 1; swapIndex++) {
            char first = word.charAt(swapIndex);
            char second = word.charAt(swapIndex + 1);
            String current = word.substring(0, swapIndex) + second + first + word.substring(swapIndex + 2);

            if (contains(current)) {
                result.add(current);
            }
        }

        return result;
    }

    /**
     * finds all valid words that can be created by inserting a single letter into word
     * @param word the word to correct
     * @return a set of valid words
     */
    private SortedSet<String> testInsertions(String word) {
        //TODO
        // When [anytime]
        // Do [adjust this function to loop through prefixes like testReplacements

        TreeSet<String> result = new TreeSet<>();

        TrieNode lastMatch = root.lastMatch(word);
        String matched = root.getMatchedPrefix(word);
        String unmatched = word.substring(matched.length());

        for (TrieNode child : lastMatch.children) {
            if (child.contains(unmatched)) {
                result.add(matched + child.letter + unmatched);
            }
        }

        return result;
    }

    /**
     * finds all valid words that can be created by replacing a single letter in word with another
     * @param word the word to correct
     * @return a set of valid words
     */
    private SortedSet<String> testReplacements(String word) {
        TreeSet<String> result = new TreeSet<>();

        String matched = root.getMatchedPrefix(word);
        for (int i = 0; i < matched.length(); i++) {
            String currentPrefix = matched.substring(0, i);
            String unmatched = word.substring(currentPrefix.length() + 1);
            TrieNode lastMatch = root.lastMatch(currentPrefix);

            for (TrieNode child : lastMatch.children) {
                if (child.contains(unmatched)) {
                    result.add(currentPrefix + child.letter + unmatched);
                }
            }
        }

        return result;
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
