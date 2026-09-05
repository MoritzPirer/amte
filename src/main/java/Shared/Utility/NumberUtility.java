package Shared.Utility;

import java.awt.*;

public class NumberUtility {
    /**
     * finds the smallest number larger than n that is a multiple of multiple
     * @param n the number to round up
     * @param multiple what number the result should be a multiple of
     * @return the smallest multiple of multiple that is greater than n
     */
    public static int nextMultipleOf(int n, int multiple) {
        return n + (multiple - n % multiple);
    }

    /**
     * converts a six-digit hex code into a color object by interpreting it as RGB-values
     * @param hex a siz digit hex code (containing only the numbers 0-9, and the letters a-f (upper- or lowercase), and
     *            optionally a leading hashtag
     * @return a Color object representing the same color or null if hex did not contain a valid hex code
     */
    public static Color hexToColor(String hex) {
        if (hex.isEmpty()) {
            return null;
        }

        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        try {
            return new Color(Integer.parseInt(hex, 16));
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * formats the given number into groups of up to groupSize digits, with each group being separated by separator.
     * grouping starts from the right (i.e. only the leftmost group can have less than groupSize digits)
     * @param number the number to format
     * @param separator the separator to insert
     * @param groupSize how many digits may be grouped together before a separator is inserted.
     * @return the formatted number
     */
    public static String formatNumber(int number, String separator, int groupSize) {
        final int BASE = 10;

        if (number == 0) {
            return "0";
        }

        StringBuilder builder = new StringBuilder();
        int remaining = number;
        int digitsInCurrentGroup = 0;
        while (remaining > 0) {
            int digit = remaining % BASE;
            builder.append(digit);

            if (++digitsInCurrentGroup == groupSize && remaining >= BASE) {
                builder.append(separator);
                digitsInCurrentGroup = 0;
            }

            remaining /= BASE;
        }

        if (number < 0) {
            builder.append("-");
        }

        return builder.reverse().toString();
    }

    public static String formatNumber(int number, String separator) {
        return formatNumber(number, separator, 3);
    }
}
