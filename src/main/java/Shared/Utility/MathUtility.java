package Shared.Utility;

import java.awt.*;

public class MathUtility {
    /**
     * finds the smallest number larger than n that is a multiple of multiple
     * @param n the number to round up
     * @param multiple what number the result should be a multiple of
     * @return the smallest multiple of multiple that is greater than n
     */
    public static int nextMultipleOf(int n, int multiple) {
        return n + (multiple - n % multiple);
    }

    public static Color hexToColor(String hex) {
        return new Color(Integer.parseInt(hex, 16));
    }
}
