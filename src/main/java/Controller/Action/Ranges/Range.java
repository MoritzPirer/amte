package Controller.Action.Ranges;

import Shared.TextPosition;

public record Range(TextPosition start, TextPosition end) {
    public Range {
        assert start != null;
        assert end != null;
        assert start.isBefore(end) || start.equals(end) : "end before start";
    }

    /**
     * creates a Range between a and b, automatically picking the one that comes first as the start
     * @param a one edge of the range
     * @param b the other edge of the range
     * @return the new range
     */
    public static Range ordered(TextPosition a, TextPosition b) {
        if (a.isBefore(b)) {
            return new Range(a, b);
        }

        return new Range(b, a);
    }
}
