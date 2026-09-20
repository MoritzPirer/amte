package SpellChecking;

import java.util.Collection;

public interface WordChecker {
    boolean contains(String word);

    void add(String word);

    void addAll(Collection<String> words);
}
