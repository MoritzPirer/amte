package Controller.Modes;

import Controller.Action.*;
import Controller.Input;
import Controller.SpecialInput;
import Controller.ViewPort;
import FileStorage.TextFile;
import LanguageSupport.LanguageProvider;
import Shared.Direction;

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
                return parseSpecialInput(input.getSpecialInput(), context.viewPort());
            }
        }

        buffer.add(input.getCharacter());
        ParseSnapshot result = state.parse(buffer, context);

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

    private Action parseSpecialInput(SpecialInput input, ViewPort viewPort) {
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
            default -> {
                return new NullAction();
            }
        }
    }
}
