package Controller.Modes;

import Controller.Action.Action;
import Controller.Action.DeleteAction;
import Controller.Action.JumpAction;
import Controller.Action.NullAction;
import Controller.Action.Ranges.FindRangeResolver;

class ExpectFindTargetState implements ChordParserState {
    private final ChordParser chordParser;
    private final ActionType actionType;
    private final boolean isReversed;
    private final boolean includeTarget;
    private final boolean changeToTypingMode;

    public ExpectFindTargetState(ChordParser chordParser, ActionType actionType, boolean isReversed, boolean includeTarget, boolean changeToTypingMode) {
        this.chordParser = chordParser;
        this.actionType = actionType;
        this.isReversed = isReversed;
        this.includeTarget = includeTarget;
        this.changeToTypingMode = changeToTypingMode;
    }

    static class Builder {
        private final ChordParser chordParser;
        private ActionType actionType;
        private boolean isReversed;
        private boolean includeTarget;
        private boolean changeToTypingMode;

        Builder(ChordParser chordParser) {
            this.chordParser = chordParser;
        }
        public Builder actionType(ActionType actionType) {
            this.actionType = actionType;
            return this;
        }
        public Builder isReversed(boolean isReversed) {
            this.isReversed = isReversed;
            return this;
        }
        public Builder includeTarget(boolean includeTarget) {
            this.includeTarget = includeTarget;
            return this;
        }
        public Builder changeToTypingMode(boolean changeToTypingMode) {
            this.changeToTypingMode = changeToTypingMode;
            return this;
        }
        public ExpectFindTargetState build() {
            return new ExpectFindTargetState(chordParser, actionType, isReversed, includeTarget, changeToTypingMode);
        }

    }

    @Override
    public ParseSnapshot parse(ParsingBuffer buffer, ParsingContext context) {
        assert buffer.length() != 0;

        char last = buffer.get().charAt(buffer.length() - 1);

        Action action;
        switch (actionType) {
            case DELETE -> {
                action = new DeleteAction(new FindRangeResolver(last, includeTarget, isReversed));
            }
            case MOVE -> {
                action = new JumpAction(new FindRangeResolver(last, includeTarget, isReversed), isReversed);
            }
            default -> {
                action = new NullAction();
            }
        }

        chordParser.changeState(new ExpectOperatorState(chordParser));
        if (changeToTypingMode) {
            context.modeManager().setMode(new TypingMode(context.modeManager()));
        }

        return new ParseSnapshot(
            ParseStatus.FINISHED,
            action
        );
    }
}
