package Controller.Action.Ranges;

import Shared.TextPosition;

public record Range(TextPosition start, TextPosition end) {
    public Range {
        assert start != null;
        assert end != null;
        assert start.isBefore(end) || start.equals(end) : "end before start";

    }
}
