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

    /**
     * checks if the range is before another range. If the first one ends at the same position where the other starts,
     * it is considered being before
     * @param range the second range
     * @return true if this range is fully before the given range, false if this range comes after or if there is any overlap
     */
    public boolean isBefore(Range range) {
        return this.end.isBefore(range.start) || this.end.equals(range.start);
    }

    public boolean isEmpty() {
        return start.equals(end);
    }

    @Override
    public String toString() {
        return start.toString() + " -> " + end.toString();
    }

}
