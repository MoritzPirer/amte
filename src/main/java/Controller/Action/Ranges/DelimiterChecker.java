package Controller.Action.Ranges;

/**
 * used by DelimiterRangeResolver. Designates when a character ends the range
 */
public interface DelimiterChecker {
    /**
     * check if the character can define the left edge (i.e. the start) of a Range
     */
    boolean isLeftDelimiter(char c);

    /**
     * check if the character can define the right edge (i.e. the end) of a Range
     */
    boolean isRightDelimiter(char c);
}
