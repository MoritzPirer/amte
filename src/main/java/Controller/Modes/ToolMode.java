package Controller.Modes;

import Controller.Action.Action;
import Controller.Action.NullAction;
import Controller.Action.QuitAction;
import Controller.Input;
import Controller.ViewPort;

public class ToolMode implements ModeState {
    private final ModeManager modeManager;

    public ToolMode(ModeManager modeManager) {
        this.modeManager = modeManager;
    }

    @Override
    public Action parseInput(Input input, ViewPort viewPort) {
        if (input.isSpecialInput()) {
            return new NullAction(); //TEMP
        }

        assert input.isSpecialInput();

        switch (input.getCharacter()) {
            case 'i' -> modeManager.setMode(new TypingMode(modeManager));
            case 'q' -> {
                //TODO WHEN [CommandMode is being implemented] DO [move quitting into CommandMode]
                return new QuitAction();
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
