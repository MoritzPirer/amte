package Controller.Modes;

import Controller.Action.Action;
import Controller.Action.DeleteAction;
import Controller.Action.Ranges.*;
import Debugging.DebugLogger;

class ExpectDelimiterState implements ChordParserState {
    private final ChordParser chordParser;
    private final ActionType actionType;
    private final boolean changeToTypingMode;
    private final boolean includeTarget;

    private ExpectDelimiterState(ChordParser chordParser, ActionType actionType, boolean changeToTypingMode, boolean includeTarget) {
        this.chordParser = chordParser;
        this.actionType = actionType;
        this.changeToTypingMode = changeToTypingMode;
        this.includeTarget = includeTarget;
    }

    static class Builder {
        private final ChordParser chordParser;
        private ActionType actionType;
        private boolean changeToTypingMode;
        private boolean includeTarget;

        public Builder(ChordParser chordParser) {
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

        public Builder includeTarget(boolean includeTarget) {
            this.includeTarget = includeTarget;
            return this;
        }

        public ExpectDelimiterState build() {
            return new ExpectDelimiterState(chordParser, actionType, changeToTypingMode, includeTarget);
        }
    }

    @Override
    public ParseSnapshot parse(ParsingBuffer buffer, ParsingContext context) {
        DebugLogger.info("PARSING", "parsing buffer " + buffer.toString() + " in ExpectDelimiterState");
        char last = adjustAliases(buffer);
        RangeResolver resolver;

        switch (last) {
            case '(', '{', '<', '[', '"', '\'' -> {
                DelimiterRangeResolver.EdgeBehaviour behaviour = includeTarget? DelimiterRangeResolver.EdgeBehaviour.ON :
                    DelimiterRangeResolver.EdgeBehaviour.BEFORE;


                resolver = DelimiterRangeResolver.makeBidirectional(
                    SpecificDelimiterChecker.matching(String.valueOf(last)),
                    behaviour
                );
            }
            case 'w' -> resolver = DelimiterRangeResolver.makeBidirectional(
                new WordDelimiterChecker(), DelimiterRangeResolver.EdgeBehaviour.ON
            );
            case 'W' -> resolver = DelimiterRangeResolver.makeBidirectional(
                new ExpressionDelimiterChecker(),
                DelimiterRangeResolver.EdgeBehaviour.ON
            );
            default -> {
                chordParser.changeState(new ExpectOperatorState(chordParser));
                return ParseSnapshot.INVALID;
            }
        }

        if (changeToTypingMode) {
            context.modeManager().setMode(new TypingMode(context.modeManager()));
        }

        chordParser.changeState(new ExpectOperatorState(chordParser));

        return switch (actionType) {
            case DELETE -> new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(resolver));
            case COPY -> throw new UnsupportedOperationException("TODO");
            default -> throw new IllegalStateException();
        };
    }

    private char adjustAliases(ParsingBuffer buffer) {
        char last = buffer.get().charAt(buffer.length() - 1);

        return switch (last) {
            case 'b' -> '(';
            case 'B' -> '{';
            case 't' -> '<';
            default -> last;
        };
    }

}
