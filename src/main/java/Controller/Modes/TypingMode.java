package Controller.Modes;

import Controller.Action.Action;
import Controller.Action.DeleteAction;
import Controller.Action.InsertAction;
import Controller.Action.NullAction;
import Controller.Input;
import Controller.ViewPort;
import Shared.TextPosition;

public class TypingMode implements ModeState {
    private final ModeManager modeManager;

    public TypingMode(ModeManager modeManager) {
        this.modeManager = modeManager;
    }

    @Override
    public Action parseInput(Input input, ViewPort viewPort) {
        if (input.isCharacter()) {
            return new InsertAction(String.valueOf(input.getCharacter()), viewPort.getLogicalCursorPosition());
        }

        assert input.isSpecialInput();

        switch (input.getSpecialInput()) {
            case ESCAPE -> {
                modeManager.setMode(new ToolMode(modeManager));
                return new NullAction();
            }
            case BACK_SPACE -> {
                TextPosition cursor = viewPort.getLogicalCursorPosition();
                if (cursor.equals(0,0)) {
                    return new NullAction();
                }

                TextPosition start;
                if (cursor.character() == 0) {
                    start = new TextPosition(
                        cursor.paragraph() - 1,
                        viewPort.lengthOfParagraph(cursor.paragraph()));
                }
                else {
                    start = new TextPosition(cursor.paragraph(), cursor.character() - 1);
                }

                return new DeleteAction(start, cursor);
            }
            default -> {
                return new NullAction();
            }
        }
    }

    @Override
    public String getName() {
        //TODO WHEN [Language Component is implemented] DO [Retrieve string from language support rather than hard-coding it]
        return "TYPING";
    }
}
