package SpellChecking;

import java.util.Collection;

public interface WordCorrecter {
    String[] correct(String word, int maxResults);
    void add(String word);
    void addAll(Collection<String> words);
}
