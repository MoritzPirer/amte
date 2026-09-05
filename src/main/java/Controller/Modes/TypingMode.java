package Controller.Modes;

import Controller.Action.Action;
import Controller.Action.DeleteAction;
import Controller.Action.InsertAction;
import Controller.Action.NullAction;
import Controller.Input;
import Controller.SpecialInput;
import Controller.ViewPort;
import FileStorage.TextFile;
import LanguageSupport.LanguageProvider;
import Shared.TextPosition;

class TypingMode implements ModeState {
    private final ModeManager modeManager;

    public TypingMode(ModeManager modeManager) {
        this.modeManager = modeManager;
    }

    @Override
    public Action parseInput(Input input, ViewPort viewPort, TextFile file) {
        if (input.isCharacter()) {
            return new InsertAction(String.valueOf(input.getCharacter()), viewPort.getLogicalCursorPosition());
        }

        return parseSpecialInput(input.getSpecialInput(), viewPort, file);
    }

    private Action parseSpecialInput(SpecialInput input, ViewPort viewPort, TextFile file) {
        switch (input) {
            case ESCAPE -> {
                modeManager.setMode(new ToolMode(modeManager));
                return new NullAction();
            }
            case BACK_SPACE -> {
                return createBackSpaceAction(viewPort, file);
            }
            case ENTER -> {
                return new InsertAction("\n",  viewPort.getLogicalCursorPosition());
            }
            default -> {
                return new NullAction();
            }
        }
    }

    private Action createBackSpaceAction(ViewPort viewPort, TextFile file) {
        TextPosition cursor = viewPort.getLogicalCursorPosition();
        TextPosition previous = file.previousPosition(cursor);
        if (previous == null) {
            return new NullAction();
        }

        return new DeleteAction(previous, cursor);
    }

    @Override
    public String getName() {
        LanguageProvider provider = new LanguageProvider();
        return provider.get("modeIdentifierTyping");
    }
}
