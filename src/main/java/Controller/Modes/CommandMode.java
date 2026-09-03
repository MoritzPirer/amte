package Controller.Modes;


import Controller.Action.Action;
import Controller.Action.NotifyAction;
import Controller.Action.NullAction;
import Controller.Input;
import Controller.SpecialInput;
import Controller.ViewPort;
import FileStorage.TextFile;
import LanguageSupport.LanguageProvider;

public class CommandMode implements ModeState {
    private final StringBuilder buffer;
    private final ModeManager modeManager;

    public CommandMode(ModeManager modeManager) {
        this.buffer = new StringBuilder();
        this.modeManager = modeManager;
    }

    @Override
    public Action parseInput(Input input, ViewPort viewPort, TextFile file) {
        if (input.isSpecialInput()) {
            switch (input.getSpecialInput()) {
                case SpecialInput.ESCAPE -> {
                    modeManager.setMode(new ToolMode(modeManager));
                    viewPort.clearTemporaryMessages();
                    return new NullAction();
                }
                case ENTER -> {
                    return processCommand();
                }
                case BACK_SPACE -> {
                    if (buffer.isEmpty()) {
                        modeManager.setMode(new ToolMode(modeManager));
                        viewPort.clearTemporaryMessages();
                        return new NullAction();
                    }
                    buffer.deleteCharAt(buffer.length() - 1);
                }
            }
        }
        else {
            buffer.append(input.getCharacter());
        }
        return showUnfinishedCommand();
    }

    private Action processCommand() {
        return new NullAction();
    }

    private Action showUnfinishedCommand() {
        return new NotifyAction(":" + buffer);
    }

    @Override
    public String getName() {
        return new LanguageProvider().get("modeIdentifierCommand");
    }
}
