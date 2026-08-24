package Controller.Modes;

import Controller.Action.Action;
import Controller.Action.InsertAction;
import Controller.Action.NullAction;
import Controller.Input;
import Controller.SpecialInput;
import Controller.ViewPort;

public class TypingMode implements ModeState {
    private final ModeManager modeManager;

    public TypingMode(ModeManager modeManager) {
        this.modeManager = modeManager;
    }

    @Override
    public Action parseInput(Input input, ViewPort viewPort) {
        if (input.isSpecialInput() && input.getSpecialInput() == SpecialInput.ESCAPE) {
            modeManager.setMode(new ToolMode(modeManager));
        }
        if (input.isCharacter()) {
            return new InsertAction(String.valueOf(input.getCharacter()), viewPort.getLogicalCursorPosition());
        }

        return new NullAction();
    }

    @Override
    public String getName() {
        //TODO WHEN [Language Component is implemented] DO [Retrieve string from language support rather than hard-coding it]
        return "TYPING";
    }
}
