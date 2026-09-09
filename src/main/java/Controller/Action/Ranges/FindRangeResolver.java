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
        if (reversed) {
            return walkBackward(viewPort, file);
        }
        else {
            return walkForward(viewPort, file);
        }
    }

    private Range walkBackward(ViewPort viewPort, TextFile file) {
        TextPosition cursor = viewPort.getLogicalCursorPosition();
        TextPosition current = cursor;
        while (true) {
            TextPosition previous = current;
            current = step(current, file);
            if (current == null) {
                return Range.ordered(cursor, previous);
            }

            char currentChar = file.readRange(current, current.offset(0, 1)).charAt(0);
            if (currentChar == target) {
                if (includeTarget) {
                    return Range.ordered(cursor, current);
                }
                else {
                    return Range.ordered(cursor, previous);
                }
            }
        }
    }

    private Range walkForward(ViewPort viewPort, TextFile file) {
        TextPosition cursor = viewPort.getLogicalCursorPosition();
        TextPosition current = cursor;
        while (true) {
            TextPosition next = step(current, file);
            if (next == null) {
                return Range.ordered(cursor, current);
            }
            current = next;

            char currentChar = file.readRange(current, current.offset(0, 1)).charAt(0);
            if (currentChar == target) {
                if (includeTarget) {
                    return Range.ordered(cursor, current);
                }
                else {
                    return Range.ordered(cursor, file.previousPosition(current));
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
}
