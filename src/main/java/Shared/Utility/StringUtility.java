package Shared.Utility;

import java.util.ArrayList;
import java.util.List;

public class StringUtility {

    /**
     * counts the number of times target occurs in text
     * @param text the string to search through
     * @param target the character to search for
     * @param beginIndex the (inclusive) index at which to start counting
     * @param endIndex the (exclusive) index at which to stop counting
     * @return the number of occurrences
     */
    public static int countOccurrences(String text, char target, int beginIndex, int endIndex) {
        if (beginIndex < 0
            || (beginIndex >= text.length() && !text.isEmpty())
            || endIndex < beginIndex
            || endIndex > text.length()) {
            throw new IllegalArgumentException("beginIndex (" + beginIndex + ") or endIndex (" + endIndex + ") out of bounds");
        }

        int count = 0;

        for (int i = beginIndex; i < endIndex; i++) {
            char current = text.charAt(i);
            if (current == target) {
                count++;
            }
        }

        return count;
    }

    /**
     * counts the number of times target occurs in text
     * @param text the string to search through
     * @param target the character to search for
     * @return the number of occurrences
     */
    public static int countOccurrences(String text, char target) {
        return countOccurrences(text, target, 0, text.length());
    }

    /**
     * counts the number of words in the text. Words are separated by at least one whitespace (as defined by Character.isWhitespace())
     * @param text the text to look through
     * @return the number of words in the text
     */
    public static int countWords(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        int currentIndex = 0;
        int wordCount = Character.isWhitespace(text.charAt(0))? 0 : 1;

        while (currentIndex + 1 < text.length()) {
            boolean currentIsWhitespace = Character.isWhitespace(text.charAt(currentIndex));
            boolean nextIsWhitespace = Character.isWhitespace(text.charAt(currentIndex + 1));

            if (currentIsWhitespace && !nextIsWhitespace) {
                wordCount++;
            }

            currentIndex++;
        }

        return wordCount;
    }

    /**
     * Splits content into a List of strings by occurrences of delimiters, with the delimiter being part of the segment before it
     * @param content the content to split up
     * @param delimiter the character to separate by
     * @return a list of split up substrings
     */
    public static List<String> splitAfterDelimiter(String content, char delimiter) {
        int start = 0;
        int end;
        List<String> lines = new ArrayList<>();

        while (content.indexOf(delimiter, start) != -1) {
            end = content.indexOf(delimiter, start) + 1;
            String line = content.substring(start, end);
            start = end;

            lines.add(line);
        }
        if (start < content.length()) {
            lines.add(content.substring(start));
        }

        return lines;
    }

    /**
     * counts the number of characters in text, not counting any character included in blacklists.
     * @param text the text to count the length of
     * @param blacklist a string containing characters to ignore in the count
     * @return the number of characters that are not in the blacklist
     */
    public static int countCharactersBlacklisted(String text, String blacklist) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (!blacklist.contains(String.valueOf(c))) {
                count++;
            }
        }

        return count;
    }

    /**
     * contains a string of length *length* with the text as a prefix, filling additional characters with padding.
     * if text is longer than length, text is returned
     * @param text the text to left-align
     * @param length the length to pad to
     * @param padding the character to pad with
     * @return the text left-aligned to length *length*, or text if length is less than the string's length
     */
    public static String leftAlign(String text, int length, char padding) {
        if (text.length() >= length) {
            return text.substring(0, length);
        }

        int padAmount = length - text.length();
        return text + String.valueOf(padding).repeat(padAmount);

    }
    public static String leftAlign(String text, int length) {
        return leftAlign(text, length, ' ');
    }

    /**
     * contains a string of length *length* with the text as a suffix, filling additional characters with padding.
     * if text is longer than length, text is returned
     * @param text the text to right-align
     * @param length the length to pad to
     * @param padding the character to pad with
     * @return the text right-aligned to length *length*, or text if length is less than the string's length
     */
    public static String rightAlign(String text, int length, char padding) {
        if (text.length() >= length) {
            return text;
        }

        int padAmount = length - text.length();
        return String.valueOf(padding).repeat(padAmount) + text;
    }

    public static String rightAlign(String text, int length) {
        return rightAlign(text, length, ' ');
    }

    /**
     * splits the given text into a list of strings where each element is at most maxLength characters long
     * @param text the text to split up
     * @param maxLength how long each split up element may be at most
     * @return a List of the split strings
     */
    public static List<String> splitByLength(String text, int maxLength) {
        List<String> lines = new ArrayList<>();
        int currentIndex = 0;

        while (currentIndex < text.length()) {
            int endIndex = currentIndex + maxLength;
            if (endIndex > text.length()) {
                lines.add(text.substring(currentIndex));
            }
            else {
                lines.add(text.substring(currentIndex, endIndex));
            }

            currentIndex = endIndex;
        }

        return lines;
    }

    public static int countLeadingWhiteSpace(String text) {
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (Character.isWhitespace(text.charAt(i))) {
                count++;
            }
            else { break; }
        }

        return count;
    }

    public static int countTrailingWhiteSpace(String text) {
        int count = 0;
        for (int i = text.length() - 1; i >= 0; i--) {
            if (Character.isWhitespace(text.charAt(i))) {
                count++;
            }
            else { break; }
        }

        return count;
    }

}
