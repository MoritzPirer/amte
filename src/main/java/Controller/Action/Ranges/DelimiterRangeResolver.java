package Controller.Action.Ranges;

import Controller.ViewPortManager.ViewPort;
import FileStorage.TextFile;
import Shared.TextPosition;

/**
 * Resolves to a range of which the end is defined by a DelimiterChecker
 */
public class DelimiterRangeResolver implements RangeResolver {
    public enum EdgeBehaviour {
        BEFORE,
        ON,
        AFTER
    }

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

    /**
     * Create a RangeResolver that resolves to a range that starts at the cursor
     * @param checker a DelimiterChecker that defines when a delimiter is encountered
     * @param behaviour a behavior that defines where the end should be placed with respect to the delimiters
     */
    public static DelimiterRangeResolver makeForward(DelimiterChecker checker, EdgeBehaviour behaviour) {
        return new DelimiterRangeResolver(checker, behaviour, true, false, false, true);
    }

    /**
     * Create a RangeResolver that resolves to a range that ends at the cursor
     * @param checker a DelimiterChecker that defines when a delimiter is encountered
     * @param behaviour a behavior that defines where the start should be placed with respect to the delimiters
     */
    public static DelimiterRangeResolver makeBackward(DelimiterChecker checker, EdgeBehaviour behaviour) {
        return new DelimiterRangeResolver(checker, behaviour, false, true, false, true);
    }

    /**
     * Create a RangeResolver that searches independently in both directions
     * @param checker a DelimiterChecker that defines when a delimiter is encountered
     * @param behaviour a behavior that defines where the start should be placed with respect to the delimiters
     */
    public static DelimiterRangeResolver makeBidirectional(DelimiterChecker checker, EdgeBehaviour behaviour) {
        return new DelimiterRangeResolver(checker, behaviour, false, false,  true, true);
    }

    /**
     * Create a RangeResolver that selects the word the cursor is currently on
     */
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
        TextPosition start = startIsCursor? cursor : walkBackward(cursor, file);
        TextPosition end = endIsCursor? cursor : walkForward(cursor, file);
        return new Range(start, end);
    }

    /**
     * find the start of the range
     * @param current where the search starts
     * @param file the file in which to search
     * @return the position to use as the start of the range
     */
    private TextPosition walkBackward(TextPosition current, TextFile file) {
        boolean hasMoved = !forceMove;
        boolean hasSeenDelimiter = false;

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

    /**
     * find the end of the range
     * @param current where the search starts
     * @param file the file in which to search
     * @return the position to use as the end start of the range
     */
    private TextPosition walkForward(TextPosition current, TextFile file) {
        boolean hasMoved = !forceMove;
        boolean hasSeenNonDelimiter = false;
        boolean hasSeenDelimiter = false;

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
