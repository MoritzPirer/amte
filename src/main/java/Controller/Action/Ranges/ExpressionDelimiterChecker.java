package Controller.Action.Ranges;

public class ExpressionDelimiterChecker implements DelimiterChecker {
    @Override
    public boolean isLeftDelimiter(char c) {
        return Character.isWhitespace(c);
    }

    @Override
    public boolean isRightDelimiter(char c) {
        return Character.isWhitespace(c);
    }

}
