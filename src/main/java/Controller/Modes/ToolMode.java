package Controller.Modes;

import Controller.Action.*;
import Controller.Action.Ranges.FixedRangeResolver;
import Controller.Input;
import Controller.SpecialInput;
import Controller.View.Line;
import Controller.ViewPort;
import Debugging.DebugLogger;
import FileStorage.TextFile;
import LanguageSupport.LanguageProvider;
import Shared.Direction;
import Shared.TextPosition;

class ToolMode implements ModeState, ChordParser {
    private final ModeManager modeManager;

    private final ParsingBuffer buffer = new ParsingBuffer(3);
    private ChordParserState state;

    public ToolMode(ModeManager modeManager) {
        this.modeManager = modeManager;
        this.state = new ExpectOperatorState(this);
    }

    @Override
    public Action parseInput(Input input, ViewPort viewPort, TextFile file) {
        ParsingContext context = new ParsingContext(viewPort, file, modeManager);

        if (input.isSpecialInput()) {
            if (buffer.length() > 0) {
                buffer.clear();
                return new NullAction();
            }
            else {
                return parseSpecialInput(input.getSpecialInput(), context.viewPort(), context.textFile());
            }
        }

        buffer.add(input.getCharacter());
        ParseSnapshot result = state.parse(buffer, context);
        DebugLogger.info("PARSING", "input chord is " + result.status()
            + ", evaluated to " + result.action().getClass().getSimpleName());

        if (result.status() != ParseStatus.UNFINISHED) {
            buffer.clear();
        }

        return result.action();
    }

    @Override
    public String getName() {
        LanguageProvider provider = new LanguageProvider();
        return provider.get("modeIdentifierTool");
    }

    @Override
    public void changeState(ChordParserState state) {
        this.state = state;
    }

    private Action parseSpecialInput(SpecialInput input, ViewPort viewPort, TextFile file) {
        switch (input) {
            case ARROW_LEFT -> {
                return new MoveAction(Direction.LEFT);
            }
            case ARROW_DOWN -> {
                return new MoveAction(Direction.DOWN);
            }
            case ARROW_UP -> {
                return new MoveAction(Direction.UP);
            }
            case ARROW_RIGHT -> {
                return new MoveAction(Direction.RIGHT);
            }
            case TAB -> {
                TextPosition position = new TextPosition(
                        viewPort.getLogicalCursorPosition().paragraph(),
                        0
                );
                return new CompoundAction(
                        new InsertAction("\t", position),
                        new JumpAction(new FixedRangeResolver(null, viewPort.getLogicalCursorPosition().offset(0, 1)))
                );
            }
            case SHIFT_TAB -> {
                int paragraph = viewPort.getLogicalCursorPosition().paragraph();
                Line line = file.readParagraph(paragraph);
                if (line.textSpans().length == 0 || !line.textSpans()[0].text().startsWith("\t")) {
                    return new NullAction();
                }

                return new DeleteAction(new FixedRangeResolver(
                        new TextPosition(paragraph, 0),
                        new TextPosition(paragraph, 1))
                );
            }

            default -> {
                return new NullAction();
            }
        }
    }
}
