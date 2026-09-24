package SpellChecking;


interface WordChecker extends WordAddable {

    /**
     * check if the word is contained in the list of known words
     * @param word the word to look for
     * @return true if the word is known, false if not
     */
    boolean contains(String word);
}
