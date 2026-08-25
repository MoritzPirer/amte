package Controller.Modes;

import Controller.Action.Action;
import Controller.Input;
import Controller.ViewPort;
import FileStorage.TextFile;

public class ModeManager {
    private ModeState currentMode;

    public ModeManager() {
        currentMode = new ToolMode(this);
    }

    public Action parseInput(Input input, ViewPort viewPort, TextFile file) {
        return currentMode.parseInput(input, viewPort, file);
    }

    public String getName() {
        return currentMode.getName();
    }

    public void setMode(ModeState mode) {
        currentMode = mode;
    }
}
