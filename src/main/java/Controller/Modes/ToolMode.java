package Controller.Modes;

import Controller.Action.*;
import Controller.Input;
import Controller.SpecialInput;
import Controller.ViewPort;
import FileStorage.TextFile;
import LanguageSupport.LanguageProvider;
import Shared.Direction;
import Shared.TextPosition;

class ToolMode implements ModeState {
    private final ModeManager modeManager;

    public ToolMode(ModeManager modeManager) {
        this.modeManager = modeManager;
    }

    @Override
    public Action parseInput(Input input, ViewPort viewPort, TextFile file) {
        if (input.isSpecialInput()) {
            return parseSpecialInput(input.getSpecialInput(), viewPort);
        }

        return parseCharacterInput(input.getCharacter(), viewPort, file);
    }

    //NOTE: viewPort will be used by later commands
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
            default -> { }
        }

        return new NullAction();
    }

    //NOTE: viewPort will be used by later commands
    private Action parseCharacterInput(char character, ViewPort viewPort, TextFile file) {
        switch (character) {
            case 'a' -> {
                modeManager.setMode(new TypingMode(modeManager));
                return new MoveAction(Direction.RIGHT);
            }
            case 'i' -> modeManager.setMode(new TypingMode(modeManager));
            case 'h' -> {
                return new MoveAction(Direction.LEFT);
            }
            case 'j' -> {
                return new MoveAction(Direction.DOWN);
            }
            case 'k' -> {
                return new MoveAction(Direction.UP);
            }
            case 'l' -> {
                return new MoveAction(Direction.RIGHT);
            }
            case 's' -> {
                modeManager.setMode(new TypingMode(modeManager));
                TextPosition cursor = viewPort.getLogicalCursorPosition();
                TextPosition next = file.nextPosition(cursor);
                return new DeleteAction(cursor, next);
            }
            case 'x' -> {
                TextPosition cursor = viewPort.getLogicalCursorPosition();
                TextPosition next = file.nextPosition(cursor);
                return new DeleteAction(cursor, next);
            }
            case 'X' -> {
                TextPosition cursor = viewPort.getLogicalCursorPosition();
                TextPosition previous = file.previousPosition(cursor);
                if (previous == null) {
                    return new NullAction();
                }

                return new DeleteAction(previous, cursor);
            }

            case ':' -> {
                modeManager.setMode(new CommandMode(modeManager));
                return new NotifyAction(":");
            }
            default -> {}
        }

        return new NullAction();

    }


    @Override
    public String getName() {
        LanguageProvider provider = new LanguageProvider();
        return provider.get("modeIdentifierTool");
    }
}
