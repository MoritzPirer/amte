package Shared.Utility;

public class StringUtility {
    public static int countOccurrences(String text, char target) {
        int count = 0;

        for (char current : text.toCharArray()) {
            if (current == target) {
                count++;
            }
        }

        return count;
    }

    public static int countWords(String text) {
        if  (text.isEmpty()) {
            return 0;
        }

        int currentIndex = 0;
        int wordCount = Character.isWhitespace(text.charAt(0))? 0 : 1;

        while (currentIndex + 1 < text.length()) {
            boolean currentIsWhitespace = Character.isWhitespace(text.charAt(currentIndex));
            boolean nextIsWhitespace = Character.isWhitespace(text.charAt(currentIndex + 1));

            if (currentIsWhitespace && nextIsWhitespace) {
                wordCount++;
            }

            currentIndex++;
        }

        return wordCount;
    }
}
