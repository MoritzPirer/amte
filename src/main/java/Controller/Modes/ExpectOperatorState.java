package Controller.Modes;

import Controller.Action.DeleteAction;
import Controller.Action.MoveAction;
import Controller.Action.NotifyAction;
import Shared.Direction;
import Shared.TextPosition;

class ExpectOperatorState implements ChordParserState {

    private final ChordParser chordParser;

    public ExpectOperatorState(ChordParser chordParser) {
        this.chordParser = chordParser;
    }

    @Override
    public ParseSnapshot parse(ParsingBuffer buffer, ParsingContext context) {
        assert buffer.length() == 1;

        switch (buffer.get().charAt(0)) {
            case 'a' -> {
                context.modeManager().setMode(new TypingMode(context.modeManager()));
                return new ParseSnapshot(
                    ParseStatus.FINISHED,
                    new MoveAction(Direction.RIGHT)
                );
            }
            case 'f' -> {
                chordParser.changeState(new ExpectFindTargetState.Builder(chordParser)
                    .actionType(ActionType.MOVE)
                    .isReversed(false)
                    .includeTarget(true)
                    .changeToTypingMode(false)
                    .build()
                );

                return ParseSnapshot.makeUnfinished(buffer.get());
            }
            case 'F' -> {
                chordParser.changeState(new ExpectFindTargetState.Builder(chordParser)
                    .actionType(ActionType.MOVE)
                    .isReversed(true)
                    .includeTarget(true)
                    .changeToTypingMode(false)
                    .build()
                );

                return ParseSnapshot.makeUnfinished(buffer.get());
            }
            case 'h' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new MoveAction(Direction.LEFT));
            }
            case 'i' ->{
                context.modeManager().setMode(new TypingMode(context.modeManager()));
                return ParseSnapshot.FINISHED_NO_OP;
            }
            case 'j' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new MoveAction(Direction.DOWN));
            }
            case 'k' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new MoveAction(Direction.UP));
            }
            case 'l' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new MoveAction(Direction.RIGHT));
            }
            case 's' -> {
                context.modeManager().setMode(new TypingMode(context.modeManager()));
                if (context.textFile().getBuffer().isEmpty()) {
                    return ParseSnapshot.FINISHED_NO_OP;
                }

                TextPosition cursor = context.viewPort().getLogicalCursorPosition();
                TextPosition next = context.textFile().nextPosition(cursor);

                return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(cursor, next));
            }
            case 't' -> {
                chordParser.changeState(new ExpectFindTargetState.Builder(chordParser)
                    .actionType(ActionType.MOVE)
                    .isReversed(false)
                    .includeTarget(false)
                    .changeToTypingMode(false)
                    .build()
                );

                return ParseSnapshot.makeUnfinished(buffer.get());
            }
            case 'T' -> {
                chordParser.changeState(new ExpectFindTargetState.Builder(chordParser)
                    .actionType(ActionType.MOVE)
                    .isReversed(true)
                    .includeTarget(false)
                    .changeToTypingMode(false)
                    .build()
                );

                return ParseSnapshot.makeUnfinished(buffer.get());
            }
            case 'x' -> {
                if (context.textFile().getBuffer().isEmpty()) {
                    return ParseSnapshot.FINISHED_NO_OP;
                }

                TextPosition cursor = context.viewPort().getLogicalCursorPosition();
                TextPosition next = context.textFile().nextPosition(cursor);

                return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(cursor, next));
            }
            case 'X' -> {
                TextPosition cursor = context.viewPort().getLogicalCursorPosition();
                TextPosition previous = context.textFile().previousPosition(cursor);
                if (previous == null) {
                    return ParseSnapshot.FINISHED_NO_OP;
                }

                return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(previous, cursor));
            }

            case ':' -> {
                context.modeManager().setMode(new CommandMode(context.modeManager()));
                return new ParseSnapshot(ParseStatus.FINISHED, new NotifyAction(":"));
            }
            default -> {
                return ParseSnapshot.INVALID;
            }
        }
    }
}
