package SpellChecking;

import Settings.SettingsMaster;
import Shared.Utility.StringUtility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record SpellChecker(WordChecker checker, WordCorrecter correcter) {
    private static final int MIN_SPELLCHECK_LENGTH = 3;

    public static boolean shouldSplit(String token) {
        if (token.length() == 1) {
            return false;
        }
        for (char c : token.toCharArray()) {
            if (!Character.isAlphabetic(c) || Character.isWhitespace(c)) {
                return true;
            }
        }

        return false;
    }

    public static String[] split(String token) {
        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();

        for (char c : token.toCharArray()) {
            if (Character.isAlphabetic(c)) {
                currentToken.append(c);
            }
            else {
                tokens.add(currentToken.toString());
                tokens.add(String.valueOf(c));
                currentToken.delete(0, currentToken.length());
            }
        }

        if (!currentToken.isEmpty()) {
            tokens.add(currentToken.toString());
        }

        return tokens.toArray(String[]::new);
    }

    public static boolean canBeSpellChecked(String token) {
        if (token.length() < MIN_SPELLCHECK_LENGTH) {
            return false;
        }

        for (char c : token.toCharArray()) {
            if (!Character.isAlphabetic(c)) {
                return false;
            }
        }

        return true;
    }

    public boolean check(String token) {
        return checker.contains(token.toLowerCase());
    }

    public boolean add(String word) {
        checker.add(word);
        correcter.add(word);

        try {
            SpellCheckerLoader.store(word, SettingsMaster.instance().getString("spellCheckLanguage"));
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }

    public String[] correct(String word, int maxResults) {
        word = StringUtility.removeWhiteSpace(word.toLowerCase());


        if (checker.contains(word)) { //nothing to correct
            return null;
        }

        return correcter.correct(word, maxResults);
    }
}
