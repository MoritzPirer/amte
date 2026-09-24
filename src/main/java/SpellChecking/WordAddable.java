package SpellChecking;

import java.util.Collection;

interface WordAddable {
    /**
     * add the given word to the list of known words. Adding a word that is already contained has no effect
     * (i.e. the method is idempotent)
     * @param word the word to add
     */
    void add(String word);

    /**
     * add all words of the collection to the list of known words. if some words are already contained, those
     * words are ignored, and all other words are added. Equivalent to calling add() for each element in words.
     * @param words the words to add
     */
    void addAll(Collection<String> words);
}
