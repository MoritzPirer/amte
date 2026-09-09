package Controller.Action.Ranges;

public class WordDelimiterChecker implements DelimiterChecker {
    @Override
    public boolean isLeftDelimiter(char c) {
        return !Character.isAlphabetic(c) && !Character.isDigit(c);
    }

    @Override
    public boolean isRightDelimiter(char c) {
        return !Character.isAlphabetic(c) && !Character.isDigit(c);
    }
}
