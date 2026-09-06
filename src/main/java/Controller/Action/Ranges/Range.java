package Controller.Action.Ranges;

import Shared.TextPosition;

public record Range(TextPosition first, TextPosition last) {
    public Range(TextPosition first, TextPosition last) {
        assert first != null;
        assert last != null;
        assert first.isBefore(last) || first.equals(last) : "end before start";

        this.first = first;
        this.last = last;
    }
}
