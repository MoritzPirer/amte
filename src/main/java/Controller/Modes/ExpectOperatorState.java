package Controller.Modes;

import Controller.Action.DeleteAction;
import Controller.Action.JumpAction;
import Controller.Action.MoveAction;
import Controller.Action.NotifyAction;
import Controller.Action.Ranges.DelimiterRangeResolver;
import Controller.Action.Ranges.ExpressionDelimiterChecker;
import Controller.Action.Ranges.RestOfParagraphRangeResolver;
import Controller.Action.Ranges.WordDelimiterChecker;
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
            case 'b' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new JumpAction(
                    DelimiterRangeResolver.makeBackward(new WordDelimiterChecker(), DelimiterRangeResolver.EdgeBehaviour.BEFORE),
                    true
                ));
            }
            case 'B' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new JumpAction(
                    DelimiterRangeResolver.makeBackward(new ExpressionDelimiterChecker(), DelimiterRangeResolver.EdgeBehaviour.BEFORE),
                    true
                ));
            }

            case 'c' -> {
                chordParser.changeState(new ExpectChordState.Builder(chordParser)
                    .actionType(ActionType.DELETE)
                    .changeToTypingMode(true)
                    .build()
                );

                return ParseSnapshot.makeUnfinished(buffer.get());
            }
            case 'C' -> {
                context.modeManager().setMode(new TypingMode(context.modeManager()));
                return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(new RestOfParagraphRangeResolver()));
            }
            case 'd' -> {
                chordParser.changeState(new ExpectChordState.Builder(chordParser)
                    .actionType(ActionType.DELETE)
                    .changeToTypingMode(false)
                    .build()
                );

                return ParseSnapshot.makeUnfinished(buffer.get());
            }
            case 'D' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new DeleteAction(new RestOfParagraphRangeResolver()));
            }
            case 'e' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new JumpAction(DelimiterRangeResolver.makeForward(
                    new WordDelimiterChecker(), DelimiterRangeResolver.EdgeBehaviour.BEFORE
                )));
            }
            case 'E' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new JumpAction(DelimiterRangeResolver.makeForward(
                    new ExpressionDelimiterChecker(), DelimiterRangeResolver.EdgeBehaviour.BEFORE
                )));
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
            case 'w' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new JumpAction(DelimiterRangeResolver.makeForward(
                    new WordDelimiterChecker(), DelimiterRangeResolver.EdgeBehaviour.AFTER
                )));
            }
            case 'W' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new JumpAction(DelimiterRangeResolver.makeForward(
                    new ExpressionDelimiterChecker(), DelimiterRangeResolver.EdgeBehaviour.AFTER
                )));
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
