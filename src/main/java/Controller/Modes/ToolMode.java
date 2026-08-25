package Controller.Modes;

import Controller.Action.Action;
import Controller.Action.MoveAction;
import Controller.Action.NullAction;
import Controller.Action.QuitAction;
import Controller.Input;
import Controller.SpecialInput;
import Controller.ViewPort;
import FileStorage.TextFile;
import Shared.Direction;

public class ToolMode implements ModeState {
    private final ModeManager modeManager;

    public ToolMode(ModeManager modeManager) {
        this.modeManager = modeManager;
    }

    @Override
    public Action parseInput(Input input, ViewPort viewPort, TextFile file) {
        if (input.isSpecialInput()) {
            return parseSpecialInput(input.getSpecialInput(), viewPort);
        }
        else {
            return parseCharacterInput(input.getCharacter(), viewPort);
        }

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
    private Action parseCharacterInput(char character, ViewPort viewPort) {
        switch (character) {
            case 'i' -> modeManager.setMode(new TypingMode(modeManager));
            case 'q' -> {
                //TODO WHEN [CommandMode is being implemented] DO [move quitting into CommandMode]
                return new QuitAction();
            }
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
            default -> {}
        }

        return new NullAction();

    }


    @Override
    public String getName() {
        //TODO WHEN [Language Component is implemented] DO [Retrieve string from language support rather than hard-coding it]
        return "TOOL";
    }
}
