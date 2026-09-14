package Controller.Modes;

import Controller.Action.*;
import Controller.Action.Ranges.FindRangeResolver;
import Controller.Services.Clipbobard.CopyType;
import Debugging.DebugLogger;

class ExpectFindTargetState implements ChordParserState {
    private final ChordParser chordParser;
    private final ActionType actionType;
    private final boolean isReversed;
    private final boolean includeTarget;
    private final boolean changeToTypingMode;

    private ExpectFindTargetState(ChordParser chordParser, ActionType actionType, boolean isReversed, boolean includeTarget, boolean changeToTypingMode) {
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
        DebugLogger.info("PARSING", "parsing buffer " + buffer.toString() + " in ExpectFindTargetState");

        assert buffer.length() != 0;

        char last = buffer.get().charAt(buffer.length() - 1);

        Action action = createActionByType(last);

        chordParser.changeState(new ExpectOperatorState(chordParser));
        if (changeToTypingMode) {
            context.modeManager().setMode(new TypingMode(context.modeManager()));
        }

        return new ParseSnapshot(ParseStatus.FINISHED, action);
    }

    private Action createActionByType(char last) {
        return switch (actionType) {
            case DELETE -> new DeleteAction(new FindRangeResolver(last, includeTarget, isReversed));
            case MOVE -> new JumpAction(new FindRangeResolver(last, includeTarget, isReversed), isReversed);
            case COPY -> new CopyAction(new FindRangeResolver(last, includeTarget, isReversed), CopyType.INLINE);
        };
    }
}
