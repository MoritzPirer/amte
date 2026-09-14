package Controller.Modes;

import Controller.Action.Action;
import Controller.Action.CopyAction;
import Controller.Action.DeleteAction;
import Controller.Action.Ranges.*;
import Controller.Services.Clipbobard.CopyType;
import Debugging.DebugLogger;
import Shared.Direction;

class ExpectChordState implements ChordParserState {
    private final ChordParser chordParser;
    private final ActionType actionType;
    private final boolean changeToTypingMode;

    private ExpectChordState(ChordParser chordParser, ActionType actionType, boolean changeToTypingMode) {
        this.chordParser = chordParser;
        this.actionType = actionType;
        this.changeToTypingMode = changeToTypingMode;
    }

    static class Builder {
        private final ChordParser chordParser;
        private ActionType actionType;
        private boolean changeToTypingMode;

        Builder(ChordParser chordParser) {
            this.chordParser = chordParser;
        }

        public Builder actionType(ActionType actionType) {
            this.actionType = actionType;
            return this;
        }

        public Builder changeToTypingMode(boolean changeToTypingMode) {
            this.changeToTypingMode = changeToTypingMode;
            return this;
        }

        public ExpectChordState build() {
            return new ExpectChordState(chordParser, actionType, changeToTypingMode);
        }
    }

    @Override
    public ParseSnapshot parse(ParsingBuffer buffer, ParsingContext context) {
        DebugLogger.info("PARSING", "parsing buffer " + buffer.toString() + " in ExpectChordState");
        assert buffer.length() == 2;

        if (buffer.get().charAt(0) == buffer.get().charAt(1)) {
            buffer.clear();
            return fullParagraphAffectingAction(context);
        }

        return switch (buffer.get().charAt(1)) {
            case 'a' ->  parseAroundModifier(buffer);
            case 'b' ->  parseStartOfWordModifier(new WordDelimiterChecker());
            case 'B' ->  parseStartOfWordModifier(new ExpressionDelimiterChecker());
            case 'e' ->  parseEndOfWordModifier(new WordDelimiterChecker());
            case 'E' ->  parseEndOfWordModifier(new ExpressionDelimiterChecker());
            case 'f' ->  parseFindModifier(buffer, Direction.RIGHT);
            case 'F' ->  parseFindModifier(buffer, Direction.LEFT);
            case 'i' ->  parseInnerModifier(buffer);
            case 't' ->  parseExclusiveFindModifier(buffer, Direction.RIGHT);
            case 'T' ->  parseExclusiveFindModifier(buffer, Direction.LEFT);
            case 'w' ->  parseNextWordModifier(new WordDelimiterChecker());
            case 'W' ->  parseNextWordModifier(new ExpressionDelimiterChecker());
            default -> parseReset();
        };
    }

    private ParseSnapshot fullParagraphAffectingAction(ParsingContext context) {
        RangeResolver resolver = new CurrentParagraphRangeResolver();
        Action action;
        switch (actionType) {
            case DELETE -> {
                if (changeToTypingMode) {
                    context.modeManager().setMode(new TypingMode(context.modeManager()));
                }

                action = new DeleteAction(resolver, CopyType.BLOCK);
            }
            case COPY -> action = new CopyAction(resolver, CopyType.BLOCK);
            default -> throw new IllegalStateException("actionType MOVE not allowed here");
        }

        chordParser.changeState(new ExpectOperatorState(chordParser));
        return new ParseSnapshot(ParseStatus.FINISHED, action);
    }

    private ParseSnapshot parseAroundModifier(ParsingBuffer buffer) {
        chordParser.changeState(new ExpectDelimiterState.Builder(chordParser)
                .actionType(actionType)
                .changeToTypingMode(changeToTypingMode)
                .includeTarget(true)
                .build()
        );
        return ParseSnapshot.makeUnfinished(buffer.get());
    }

    private ParseSnapshot parseInnerModifier(ParsingBuffer buffer) {
        chordParser.changeState(new ExpectDelimiterState.Builder(chordParser)
                .actionType(actionType)
                .changeToTypingMode(changeToTypingMode)
                .includeTarget(false)
                .build()
        );
        return ParseSnapshot.makeUnfinished(buffer.get());
    }

    ///

    private ParseSnapshot parseStartOfWordModifier(DelimiterChecker checker) {
        chordParser.changeState(new ExpectOperatorState(chordParser));
        return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(
            DelimiterRangeResolver.makeBackward(checker, DelimiterRangeResolver.EdgeBehaviour.BEFORE),
            CopyType.INLINE
        ));
    }

    private ParseSnapshot parseEndOfWordModifier(DelimiterChecker checker) {
        chordParser.changeState(new ExpectOperatorState(chordParser));
        return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(
                DelimiterRangeResolver.makeForward(checker, DelimiterRangeResolver.EdgeBehaviour.BEFORE),
                CopyType.INLINE
        ));
    }

    private ParseSnapshot parseNextWordModifier(DelimiterChecker checker) {
        chordParser.changeState(new ExpectOperatorState(chordParser));
        return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(
                DelimiterRangeResolver.makeForward(checker, DelimiterRangeResolver.EdgeBehaviour.AFTER),
                CopyType.INLINE
        ));
    }

    private ParseSnapshot parseFindModifier(ParsingBuffer buffer, Direction direction) {
        chordParser.changeState(new ExpectFindTargetState.Builder(chordParser)
                .actionType(actionType)
                .isReversed(direction == Direction.LEFT)
                .includeTarget(true)
                .changeToTypingMode(changeToTypingMode)
                .build()
        );

        return ParseSnapshot.makeUnfinished(buffer.get());
    }

    private ParseSnapshot parseExclusiveFindModifier(ParsingBuffer buffer, Direction direction) {
        chordParser.changeState(new ExpectFindTargetState.Builder(chordParser)
                .actionType(actionType)
                .isReversed(direction == Direction.LEFT)
                .includeTarget(false)
                .changeToTypingMode(changeToTypingMode)
                .build()
        );

        return ParseSnapshot.makeUnfinished(buffer.get());
    }

    private ParseSnapshot parseReset() {
        chordParser.changeState(new ExpectOperatorState(chordParser));
        return ParseSnapshot.INVALID;
    }

}
