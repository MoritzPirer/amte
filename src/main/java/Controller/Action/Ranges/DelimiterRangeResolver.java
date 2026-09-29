package Controller.Action.Ranges;

import Controller.ViewPortManager.ViewPort;
import FileStorage.TextFile;
import Shared.TextPosition;

public class DelimiterRangeResolver implements RangeResolver {
    public enum EdgeBehaviour {
        BEFORE,
        ON,
        AFTER
    };

    private final DelimiterChecker checker;
    private final EdgeBehaviour behaviour;
    private final boolean startIsCursor;
    private final boolean endIsCursor;
    private final boolean accountForExclusiveEnd;
    private final boolean forceMove;

    private DelimiterRangeResolver(
        DelimiterChecker checker,
        EdgeBehaviour behaviour,
        boolean startIsCursor,
        boolean endIsCursor,
        boolean accountForExclusiveEnd,
        boolean forceMove
    ) {
        this.checker = checker;
        this.behaviour = behaviour;
        this.startIsCursor = startIsCursor;
        this.endIsCursor = endIsCursor;
        this.accountForExclusiveEnd = accountForExclusiveEnd;
        this.forceMove = forceMove;
    }

    public static DelimiterRangeResolver makeForward(DelimiterChecker checker, EdgeBehaviour behaviour) {
        return new DelimiterRangeResolver(checker, behaviour, true, false, false, true);
    }

    public static DelimiterRangeResolver makeBackward(DelimiterChecker checker, EdgeBehaviour behaviour) {
        return new DelimiterRangeResolver(checker, behaviour, false, true, false, true);
    }

    public static DelimiterRangeResolver makeBidirectional(DelimiterChecker checker, EdgeBehaviour behaviour) {
        return new DelimiterRangeResolver(checker, behaviour, false, false,  true, true);
    }

    public static DelimiterRangeResolver makeWordUnderCursor() {
        return new DelimiterRangeResolver(
            new WordDelimiterChecker(),
            EdgeBehaviour.BEFORE,
            false,
            false,
            true,
            false
        );
    }

    @Override
    public Range resolve(ViewPort viewPort, TextFile file) {
        TextPosition cursor = viewPort.getLogicalCursorPosition();
        TextPosition start = startIsCursor? cursor : walkBackward(viewPort, file);
        TextPosition end = endIsCursor? cursor : walkForward(viewPort, file);
        return new Range(start, end);
    }

    private TextPosition walkBackward(ViewPort viewPort, TextFile file) {
        boolean hasMoved = !forceMove;
        boolean hasSeenDelimiter = false;

        TextPosition current = viewPort.getLogicalCursorPosition();
        while (true) {
            TextPosition previous = current;
            current = file.previousPosition(current);

            if (current == null) {
                return previous;
            }

            char currentChar = file.readRange(current, current.offset(0, 1)).charAt(0);
            if (checker.isLeftDelimiter(currentChar)) {
                hasSeenDelimiter = true;
                switch (behaviour) {
                    case BEFORE -> {
                        if (hasMoved) {
                            return previous;
                        }
                    }
                    case ON -> {
                        return current;
                    }
                    case AFTER -> { }
                }
            }
            else {
                if (behaviour == EdgeBehaviour.AFTER && hasSeenDelimiter) {
                    return current;
                }
            }

            hasMoved = true;
        }
    }

    private TextPosition walkForward(ViewPort viewPort, TextFile file) {
        boolean hasMoved = !forceMove;
        boolean hasSeenNonDelimiter = false;
        boolean hasSeenDelimiter = false;

        TextPosition current = viewPort.getLogicalCursorPosition();

        while (true) {
            TextPosition next = stepForward(current, file);
            if (next == null) {
                return current;
            }
            current = next;

            char currentChar = file.readRange(current, current.offset(0, 1)).charAt(0);

            if (checker.isRightDelimiter(currentChar)) {
                hasSeenDelimiter = true;
                switch (behaviour) {
                    case BEFORE -> {
                        if (hasMoved && hasSeenNonDelimiter) {
                            if (accountForExclusiveEnd) {
                                return current;
                            }
                            return file.previousPosition(current);
                        }
                    }
                    case ON -> {
                        if (accountForExclusiveEnd) {
                            return file.nextPosition(current);
                        }
                        return current;
                    }
                    case AFTER -> { }
                }
            }
            else {
                hasSeenNonDelimiter = true;
                if (behaviour == EdgeBehaviour.AFTER && hasSeenDelimiter) {
                    return current;
                }
            }

            hasMoved = true;
        }
    }

    private TextPosition stepForward(TextPosition current, TextFile file) {
        TextPosition nextPosition = file.nextPosition(current);
        if (file.positionExists(nextPosition)) {
            return nextPosition;
        }

        return null;
    }
}
