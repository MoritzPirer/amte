package SpellChecking;

interface WordCorrecter extends WordAddable {

    /**
     * find up to maxResults corrections for word
     * @param word the word to correct
     * @param maxResults the maximum amount of results to find
     * @return an array of up to maxResults elements that are recommended corrections to word, in no
     * specific order
     */
    String[] correct(String word, int maxResults);
}
