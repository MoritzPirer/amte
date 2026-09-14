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

        switch (buffer.get().charAt(0)) {
            case 'a' -> {
                context.modeManager().setMode(new TypingMode(context.modeManager()));
                return new ParseSnapshot(
                    ParseStatus.FINISHED,
                    new MoveAction(Direction.RIGHT)
                );
            }
            case 'A' -> {
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
            case 'i' -> {
                context.modeManager().setMode(new TypingMode(context.modeManager()));
                return ParseSnapshot.FINISHED_NO_OP;
            }
            case 'I' -> {
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
            case 'j' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new MoveAction(Direction.DOWN));
            }
            case 'J' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new ParagraphJoiningAction(
                    context.viewPort().getLogicalCursorPosition().paragraph()
                ));
            }
            case 'k' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new MoveAction(Direction.UP));
            }
            case 'l' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new MoveAction(Direction.RIGHT));
            }
            case 'o' -> {
                context.modeManager().setMode(new TypingMode(context.modeManager()));

                int paragraph = context.viewPort().getLogicalCursorPosition().paragraph();
                int character = context.textFile().lengthOfParagraph(paragraph) - 1;
                return new ParseSnapshot(ParseStatus.FINISHED,
                    new InsertAction("\n", new TextPosition(paragraph, character))
                );
            }
            case 'O' -> {
                context.modeManager().setMode(new TypingMode(context.modeManager()));

                int paragraph = context.viewPort().getLogicalCursorPosition().paragraph();
                return new ParseSnapshot(ParseStatus.FINISHED, new CompoundAction(
                    new InsertAction("\n", new TextPosition(paragraph, 0)),
                    new MoveAction(Direction.UP)
                ));
            }
            case 'p' -> {
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
            case 'P' -> {
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
            case 'u' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new UndoAction());
            }
            case 'U' -> {
                return new ParseSnapshot(ParseStatus.FINISHED, new RedoAction());
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
            case 'y' -> {
                chordParser.changeState(new ExpectChordState.Builder(chordParser)
                        .actionType(ActionType.COPY)
                        .changeToTypingMode(false)
                        .build()
                );

                return ParseSnapshot.makeUnfinished(buffer.get());
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
