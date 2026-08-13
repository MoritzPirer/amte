package Shared.Utility;

import java.util.ArrayList;
import java.util.List;

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

    public static List<String> splitAfterDelimiter(String content, String delimiter) {
        int start = 0;
        int end;
        List<String> lines = new ArrayList<>();

        while (content.indexOf(delimiter, start) != -1) {
            end = content.indexOf(delimiter, start) + 1;
            String line = content.substring(start, end);
            start = end;

            lines.add(line);
        }
        if (start <= content.length()) {
            lines.add(content.substring(start));
        }

        //System.out.println(content + " split into " + lines);
        return lines;
    }
}
