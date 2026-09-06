package Controller.Action.Ranges;

import Controller.ViewPort;
import FileStorage.TextFile;
import Shared.TextPosition;

public class FindRangeResolver implements RangeResolver {
    private final char target;
    private final boolean includeTarget;
    private final boolean reversed;

    public FindRangeResolver(char target, boolean includeTarget, boolean reversed) {
        this.target = target;
        this.includeTarget = includeTarget;
        this.reversed = reversed;
    }

    @Override
    public Range resolve(ViewPort viewPort, TextFile file) {
        TextPosition cursor = viewPort.getLogicalCursorPosition();
        TextPosition current = cursor;
        while (true) {
            TextPosition previous = current;
            current = step(current, file);
            if (current == null) {
                return toRange(cursor, previous);
            }

            char currentChar = file.readRange(current, current).charAt(0);
            if (currentChar == target) {
                if (includeTarget) {
                    return toRange(cursor, current);
                }
                else {
                    return toRange(cursor, previous);
                }
            }
        }
    }

    private TextPosition step(TextPosition current, TextFile file) {
        if (reversed) {
            return file.previousPosition(current);
        }

        TextPosition nextPosition = file.nextPosition(current);
        if (file.positionExists(nextPosition)) {
            return nextPosition;
        }

        return null;
    }

    private Range toRange(TextPosition cursor, TextPosition current) {
        if (cursor.isBefore(current)) {
            return new Range(cursor, current);
        }

        return new Range(current, cursor);
    }
}
