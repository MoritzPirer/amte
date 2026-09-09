package Controller.Action.Ranges;

public class SpecificDelimiterChecker implements DelimiterChecker {
    private final String leftDelimiters;
    private final String rightDelimiters;

    public SpecificDelimiterChecker(String leftDelimiters, String rightDelimiters) {
        this.leftDelimiters = leftDelimiters;
        this.rightDelimiters = rightDelimiters;
    }

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
