package Controller.Modes;

import Controller.Action.Action;
import Controller.Action.DeleteAction;
import Controller.Action.Ranges.CurrentParagraphRangeResolver;
import Controller.Action.Ranges.RangeResolver;

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
        assert buffer.length() == 2;

        if (buffer.get().charAt(0) == buffer.get().charAt(1)) {
            return fullParagraphAffectingAction(context);
        }

        switch (buffer.get().charAt(1)) {
            case 'i', 'a' -> throw new UnsupportedOperationException("TODO");
            case 'w', 'W', 'b', 'B', 'e', 'E' -> throw new UnsupportedOperationException("TO BE DONE");
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

            default -> { return ParseSnapshot.INVALID; }
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
            //TODO COPY
            default -> {
                throw new IllegalStateException("actionType MOVE not allowed here");
            }
        }

        return new ParseSnapshot(ParseStatus.FINISHED, action);
    }
}
