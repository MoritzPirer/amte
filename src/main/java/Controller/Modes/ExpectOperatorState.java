package Controller.Modes;

import Controller.Action.*;
import Controller.Action.Ranges.*;
import Debugging.DebugLogger;
import Shared.Direction;
import Shared.TextPosition;

class ExpectOperatorState implements ChordParserState {

    private final ChordParser chordParser;

    public ExpectOperatorState(ChordParser chordParser) {
        this.chordParser = chordParser;
    }

    @Override
    public ParseSnapshot parse(ParsingBuffer buffer, ParsingContext context) {
        DebugLogger.info("PARSING", "parsing buffer " + buffer.toString() + " in ExpectOperatorState");

        assert buffer.length() == 1;

        return switch (buffer.get().charAt(0)) {
            case 'a' -> parseTypingModeAfterCursor(context);
            case 'A' -> parseTypingModeAfterParagraph(context);
            case 'b' -> parseStartOfWordMove(new WordDelimiterChecker());
            case 'B' -> parseStartOfWordMove(new ExpressionDelimiterChecker());
            case 'c' -> parseBeginningOfDeleteChord(buffer, true);
            case 'C' -> parseRestOfParagraphDelete(context, true);
            case 'd' -> parseBeginningOfDeleteChord(buffer, false);
            case 'D' -> parseRestOfParagraphDelete(context, false);
            case 'e' -> parseEndOfWordMove(new WordDelimiterChecker());
            case 'E' -> parseEndOfWordMove(new ExpressionDelimiterChecker());
            case 'f' -> parseFindOperator(buffer, Direction.RIGHT);
            case 'F' -> parseFindOperator(buffer, Direction.LEFT);
            case 'h' -> new ParseSnapshot(ParseStatus.FINISHED, new MoveAction(Direction.LEFT));
            case 'i' -> parseTypingModeBeforeCursor(context);
            case 'I' -> parseTypingModeBeforeParagraph(context);
            case 'j' -> new ParseSnapshot(ParseStatus.FINISHED, new MoveAction(Direction.DOWN));
            case 'J' -> joinParagraphs(context);
            case 'k' -> new ParseSnapshot(ParseStatus.FINISHED, new MoveAction(Direction.UP));
            case 'l' -> new ParseSnapshot(ParseStatus.FINISHED, new MoveAction(Direction.RIGHT));
            case 'o' -> parseCreateParagraphBelow(context);
            case 'O' -> parseCreateParagraphAbove(context);
            case 'p' -> parsePasteBelow(context);
            case 'P' -> parsePasteAbove(context);
            case 's' -> parseCutUnderCursor(context, true);
            case 't' -> parseExclusiveFindOperator(buffer, Direction.RIGHT);
            case 'T' -> parseExclusiveFindOperator(buffer, Direction.LEFT);
            case 'u' -> new ParseSnapshot(ParseStatus.FINISHED, new UndoAction());
            case 'U' -> new ParseSnapshot(ParseStatus.FINISHED, new RedoAction());
            case 'w' -> parseNextWordMove(new WordDelimiterChecker());
            case 'W' -> parseNextWordMove(new ExpressionDelimiterChecker());
            case 'x' -> parseCutUnderCursor(context, false);
            case 'X' -> parseCutBeforeCursor(context);
            case 'y' -> parseBeginningOfCopyChord(buffer);
            case ':' -> enterCommandMode(context);
            default -> ParseSnapshot.INVALID;
        };
    }

    private ParseSnapshot parseTypingModeAfterCursor(ParsingContext context) {
        context.modeManager().setMode(new TypingMode(context.modeManager()));
        return new ParseSnapshot(
            ParseStatus.FINISHED,
            new MoveAction(Direction.RIGHT)
        );
    }

    private ParseSnapshot parseTypingModeBeforeCursor(ParsingContext context) {
        context.modeManager().setMode(new TypingMode(context.modeManager()));
        return ParseSnapshot.FINISHED_NO_OP;
    }

    private ParseSnapshot parseTypingModeAfterParagraph(ParsingContext context) {
        context.modeManager().setMode(new TypingMode(context.modeManager()));
        int paragraph = context.viewPort().getLogicalCursorPosition().paragraph();
        int character = context.textFile().lengthOfParagraph(paragraph) - 1;

        return new ParseSnapshot(ParseStatus.FINISHED,
            new JumpAction(
                new FixedRangeResolver( null, new TextPosition(paragraph, character)),
                false
            )
        );
    }

    private ParseSnapshot parseTypingModeBeforeParagraph(ParsingContext context) {
        context.modeManager().setMode(new TypingMode(context.modeManager()));
        return new ParseSnapshot(ParseStatus.FINISHED,
            new JumpAction(
                new FixedRangeResolver(
                    new TextPosition(context.viewPort().getLogicalCursorPosition().paragraph(), 0),
                    null
                ),
                true
            )
        );
    }

    private ParseSnapshot parseStartOfWordMove(DelimiterChecker checker) {
        return new ParseSnapshot(ParseStatus.FINISHED, new JumpAction(
            DelimiterRangeResolver.makeBackward(checker, DelimiterRangeResolver.EdgeBehaviour.BEFORE),
            true
        ));
    }

    private ParseSnapshot parseEndOfWordMove(DelimiterChecker checker) {
        return new ParseSnapshot(ParseStatus.FINISHED, new JumpAction(DelimiterRangeResolver.makeForward(
                checker, DelimiterRangeResolver.EdgeBehaviour.BEFORE
        )));
    }

    private ParseSnapshot parseNextWordMove(DelimiterChecker checker) {
        return new ParseSnapshot(ParseStatus.FINISHED, new JumpAction(DelimiterRangeResolver.makeForward(
                checker, DelimiterRangeResolver.EdgeBehaviour.AFTER
        )));
    }

    private ParseSnapshot parseBeginningOfDeleteChord(ParsingBuffer buffer, boolean changeToTypingMode) {
        chordParser.changeState(new ExpectChordState.Builder(chordParser)
                .actionType(ActionType.DELETE)
                .changeToTypingMode(changeToTypingMode)
                .build()
        );

        return ParseSnapshot.makeUnfinished(buffer.get());
    }

    private ParseSnapshot parseBeginningOfCopyChord(ParsingBuffer buffer) {
        chordParser.changeState(new ExpectChordState.Builder(chordParser)
                .actionType(ActionType.COPY)
                .changeToTypingMode(false)
                .build()
        );

        return ParseSnapshot.makeUnfinished(buffer.get());
    }

    private ParseSnapshot parseRestOfParagraphDelete(ParsingContext context, boolean changeToTypingMode) {
        if (changeToTypingMode) {
            context.modeManager().setMode(new TypingMode(context.modeManager()));
        }
        return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(new RestOfParagraphRangeResolver()));
    }

    private ParseSnapshot parseFindOperator(ParsingBuffer buffer, Direction direction) {
        chordParser.changeState(new ExpectFindTargetState.Builder(chordParser)
                .actionType(ActionType.MOVE)
                .isReversed(direction == Direction.LEFT)
                .includeTarget(true)
                .changeToTypingMode(false)
                .build()
        );

        return ParseSnapshot.makeUnfinished(buffer.get());
    }

    private ParseSnapshot parseExclusiveFindOperator(ParsingBuffer buffer, Direction direction) {
        chordParser.changeState(new ExpectFindTargetState.Builder(chordParser)
                .actionType(ActionType.MOVE)
                .isReversed(direction == Direction.LEFT)
                .includeTarget(false)
                .changeToTypingMode(false)
                .build()
        );

        return ParseSnapshot.makeUnfinished(buffer.get());
    }

    private ParseSnapshot parseCreateParagraphBelow(ParsingContext context) {
        context.modeManager().setMode(new TypingMode(context.modeManager()));

        int paragraph = context.viewPort().getLogicalCursorPosition().paragraph();
        int character = context.textFile().lengthOfParagraph(paragraph) - 1;
        return new ParseSnapshot(ParseStatus.FINISHED,
                new InsertAction("\n", new TextPosition(paragraph, character))
        );
    }

    private ParseSnapshot parseCreateParagraphAbove(ParsingContext context) {
        context.modeManager().setMode(new TypingMode(context.modeManager()));

        int paragraph = context.viewPort().getLogicalCursorPosition().paragraph();
        int character = context.textFile().lengthOfParagraph(paragraph) - 1;
        return new ParseSnapshot(ParseStatus.FINISHED,
                new InsertAction("\n", new TextPosition(paragraph, character))
        );
    }

    private ParseSnapshot parsePasteBelow(ParsingContext context) {
        String content = context.viewPort().getInternalClipboard();

        switch (context.viewPort().getInternalClipboardType()) {
            case BLOCK -> {
                int paragraph = context.viewPort().getLogicalCursorPosition().paragraph() + 1;
                return new ParseSnapshot(ParseStatus.FINISHED,
                        new InsertAction(content, new TextPosition(paragraph, 0))
                );
            }
            case INLINE -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new CompoundAction(
                        new MoveAction(Direction.RIGHT),
                        new InsertAction(content, context.viewPort().getLogicalCursorPosition())
                ));
            }
            case null -> { return ParseSnapshot.FINISHED_NO_OP; }
        }
    }

    private ParseSnapshot parsePasteAbove(ParsingContext context) {
        String content = context.viewPort().getInternalClipboard();

        switch (context.viewPort().getInternalClipboardType()) {
            case BLOCK -> {

                int paragraph = context.viewPort().getLogicalCursorPosition().paragraph();
                return new ParseSnapshot(ParseStatus.FINISHED,
                        new InsertAction(content, new TextPosition(paragraph, 0))
                );
            }
            case INLINE -> {
                return new ParseSnapshot(ParseStatus.FINISHED,
                        new InsertAction(content, context.viewPort().getLogicalCursorPosition())
                );
            }
            case null -> { return ParseSnapshot.FINISHED_NO_OP; }
        }
    }

    private ParseSnapshot parseCutUnderCursor(ParsingContext context, boolean changeToTypingMode) {
        if (changeToTypingMode) {
            context.modeManager().setMode(new TypingMode(context.modeManager()));
        }
        if (context.textFile().getBuffer().isEmpty()) {
            return ParseSnapshot.FINISHED_NO_OP;
        }

        TextPosition cursor = context.viewPort().getLogicalCursorPosition();
        TextPosition next = context.textFile().nextPosition(cursor);

        return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(cursor, next));
    }

    private ParseSnapshot parseCutBeforeCursor(ParsingContext context) {
        TextPosition cursor = context.viewPort().getLogicalCursorPosition();
        TextPosition previous = context.textFile().previousPosition(cursor);
        if (previous == null) {
            return ParseSnapshot.FINISHED_NO_OP;
        }

        return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(previous, cursor));
    }

    private ParseSnapshot enterCommandMode(ParsingContext context) {
        context.modeManager().setMode(new CommandMode(context.modeManager()));
        return new ParseSnapshot(ParseStatus.FINISHED, new NotifyAction(":"));
    }

    private ParseSnapshot joinParagraphs(ParsingContext context) {
        return new ParseSnapshot(ParseStatus.FINISHED, new ParagraphJoiningAction(
                context.viewPort().getLogicalCursorPosition().paragraph()
        ));
    }
}
