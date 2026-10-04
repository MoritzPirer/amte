package Controller.Action.Ranges;

/**
    checks the requested characters against a set of delimiter characters defined at construction
 */
public class SpecificDelimiterChecker implements DelimiterChecker {
    private final String leftDelimiters;
    private final String rightDelimiters;

    public SpecificDelimiterChecker(String leftDelimiters, String rightDelimiters) {
        this.leftDelimiters = leftDelimiters;
        this.rightDelimiters = rightDelimiters;
    }

    /**
     * Deduces the right Delimiter from the left delimiter and creates a corresponding object
     * @param leftDelimiters the left delimiters to use. Must be one of: ([{<"'
     */
    public static SpecificDelimiterChecker matching(String leftDelimiters) {
        if (leftDelimiters.length() != 1) {
            throw new IllegalArgumentException("Left delimiter must have exactly one character");
        }

        return switch (leftDelimiters.charAt(0)) {
            case '(' -> new SpecificDelimiterChecker("(", ")");
            case '[' -> new SpecificDelimiterChecker("[", "]");
            case '{' -> new SpecificDelimiterChecker("{", "}");
            case '<' -> new SpecificDelimiterChecker("<", ">");
            case '"' -> new SpecificDelimiterChecker("\"", "\"");
            case '\'' -> new SpecificDelimiterChecker("'", "'");
            default -> throw new IllegalArgumentException("Invalid left delimiter");
        };
    }

    @Override
    public boolean isLeftDelimiter(char c) {
        return leftDelimiters.contains(String.valueOf(c));
    }
    @Override
    public boolean isRightDelimiter(char c) {
        return rightDelimiters.contains(String.valueOf(c));
    }
}
