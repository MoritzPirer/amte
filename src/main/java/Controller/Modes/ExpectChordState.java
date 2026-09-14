package Controller.Modes;

import Controller.Action.Action;
import Controller.Action.CopyAction;
import Controller.Action.DeleteAction;
import Controller.Action.Ranges.*;
import Controller.Services.Clipbobard.CopyType;
import Debugging.DebugLogger;

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

        switch (buffer.get().charAt(1)) {
            case 'a' -> {
                chordParser.changeState(new ExpectDelimiterState.Builder(chordParser)
                    .actionType(actionType)
                    .changeToTypingMode(changeToTypingMode)
                    .includeTarget(true)
                    .build()
                );
                return ParseSnapshot.makeUnfinished(buffer.get());
            }
            case 'b' -> {
                chordParser.changeState(new ExpectOperatorState(chordParser));
                return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(
                    DelimiterRangeResolver.makeBackward(new WordDelimiterChecker(), DelimiterRangeResolver.EdgeBehaviour.BEFORE)
                ));
            }
            case 'B' -> {
                chordParser.changeState(new ExpectOperatorState(chordParser));
                return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(
                    DelimiterRangeResolver.makeBackward(new ExpressionDelimiterChecker(), DelimiterRangeResolver.EdgeBehaviour.BEFORE)
                ));
            }
            case 'e' -> {
                chordParser.changeState(new ExpectOperatorState(chordParser));
                return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(
                        DelimiterRangeResolver.makeForward(new WordDelimiterChecker(), DelimiterRangeResolver.EdgeBehaviour.BEFORE)
                ));
            }
            case 'E' -> {
                chordParser.changeState(new ExpectOperatorState(chordParser));
                return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(
                        DelimiterRangeResolver.makeForward(new ExpressionDelimiterChecker(), DelimiterRangeResolver.EdgeBehaviour.BEFORE)
                ));
            }
            case 'f' -> {
                chordParser.changeState(new ExpectFindTargetState.Builder(chordParser)
                    .actionType(actionType)
                    .isReversed(false)
                    .includeTarget(true)
                    .changeToTypingMode(changeToTypingMode)
                    .build()
                );

                return ParseSnapshot.makeUnfinished(buffer.get());
            }
            case 'F' -> {
                chordParser.changeState(new ExpectFindTargetState.Builder(chordParser)
                        .actionType(actionType)
                        .isReversed(true)
                        .includeTarget(true)
                        .changeToTypingMode(changeToTypingMode)
                        .build()
                );

                return ParseSnapshot.makeUnfinished(buffer.get());
            }
            case 'i' -> {
                chordParser.changeState(new ExpectDelimiterState.Builder(chordParser)
                        .actionType(actionType)
                        .changeToTypingMode(changeToTypingMode)
                        .includeTarget(false)
                        .build()
                );
                return ParseSnapshot.makeUnfinished(buffer.get());
            }
            case 't' -> {
                chordParser.changeState(new ExpectFindTargetState.Builder(chordParser)
                    .actionType(actionType)
                    .isReversed(false)
                    .includeTarget(false)
                    .changeToTypingMode(changeToTypingMode)
                    .build()
                );

                return ParseSnapshot.makeUnfinished(buffer.get());
            }
            case 'T' -> {
                chordParser.changeState(new ExpectFindTargetState.Builder(chordParser)
                        .actionType(actionType)
                        .isReversed(true)
                        .includeTarget(false)
                        .changeToTypingMode(changeToTypingMode)
                        .build()
                );

                return ParseSnapshot.makeUnfinished(buffer.get());
            }
            case 'w' -> {
                chordParser.changeState(new ExpectOperatorState(chordParser));
                return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(
                        DelimiterRangeResolver.makeForward(new WordDelimiterChecker(), DelimiterRangeResolver.EdgeBehaviour.AFTER)
                ));
            }
            case 'W' -> {
                chordParser.changeState(new ExpectOperatorState(chordParser));
                return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(
                        DelimiterRangeResolver.makeForward(new ExpressionDelimiterChecker(), DelimiterRangeResolver.EdgeBehaviour.AFTER)
                ));
            }

            default -> {
                chordParser.changeState(new ExpectOperatorState(chordParser));
                return ParseSnapshot.INVALID;
            }
        }
    }

    private ParseSnapshot fullParagraphAffectingAction(ParsingContext context) {
        RangeResolver resolver = new CurrentParagraphRangeResolver();
        Action action;
        switch (actionType) {
            case DELETE -> {
                if (changeToTypingMode) {
                    context.modeManager().setMode(new TypingMode(context.modeManager()));
                }

                action = new DeleteAction(resolver);
            }
            case COPY -> {
                action = new CopyAction(resolver, CopyType.BLOCK);
            }
            default -> throw new IllegalStateException("actionType MOVE not allowed here");
        }

        chordParser.changeState(new ExpectOperatorState(chordParser));
        return new ParseSnapshot(ParseStatus.FINISHED, action);
    }
}
