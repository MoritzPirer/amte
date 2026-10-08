package Controller.Modes;

import Controller.Action.*;
import Controller.Input;
import Controller.SpecialInput;
import Controller.ViewPortManager.ViewPort;
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
                reset(viewPort);
                return new NullAction();
            }
            else {
                return parseSpecialInput(input.getSpecialInput(), context.viewPort(), context.textFile());
            }
        }

        buffer.add(input.getCharacter());
        ParseSnapshot result = state.parse(buffer, context);

        DebugLogger.info("PARSING", "input chord '" + result.status()
            + "' evaluated to '" + result.action().getClass().getSimpleName() + "'");

        if (result.status() != ParseStatus.UNFINISHED) {
            buffer.clear();
        }

        return result.action();
    }

    @Override
    public String getName() {
        LanguageProvider provider = new LanguageProvider();
        return provider.get("ui/mode/tool");
    }

    @Override
    public void changeState(ChordParserState state) {
        this.state = state;
    }

    private Action parseSpecialInput(SpecialInput input, ViewPort viewPort, TextFile file) {
        reset(viewPort);

        switch (input) {
            case ARROW_LEFT -> { return new MoveAction(Direction.LEFT); }
            case ARROW_DOWN -> { return new MoveAction(Direction.DOWN); }
            case ARROW_UP -> { return new MoveAction(Direction.UP); }
            case ARROW_RIGHT -> { return new MoveAction(Direction.RIGHT); }
            case TAB -> { return createIndentAction(viewPort); }
            case SHIFT_TAB -> {
                TextPosition cursor = viewPort.getLogicalCursorPosition();
                String paragraph = file.readRawParagraph(cursor.paragraph());

                if (!paragraph.startsWith("\t")) {
                    return new NullAction();
                }

                return new CompoundAction(
                    new DeleteAction(
                        new TextPosition(cursor.paragraph(), 0),
                        new TextPosition(cursor.paragraph(), 1)
                    ),
                    new JumpAction(cursor.offset(0, -1))
                );
            }

            default -> { return new NullAction(); }
        }
    }

private Action createIndentAction(ViewPort viewPort) {
        TextPosition position = new TextPosition(viewPort.getLogicalCursorPosition().paragraph(), 0);

        return new CompoundAction(
            new InsertAction("\t", position),
            new JumpAction(viewPort.getLogicalCursorPosition().offset(0, 1)
        ));
    }

    private void reset(ViewPort viewPort) {
        changeState(new ExpectOperatorState(this));
        viewPort.refreshTemporaryMessages();
    }
}
