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

    public static String formatNumber(int number) {
        return formatNumber(number, " ", 3);
    }

    public static String formatNumber(int number, String separator) {
        return formatNumber(number, separator, 3);
    }

    public static String formatNumber(int number, String separator, int groupSize) {
        final int BASE = 10;

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
}
