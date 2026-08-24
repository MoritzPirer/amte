package Controller.Modes;

import Controller.Action.Action;
import Controller.Input;
import Controller.ViewPort;

public class ModeManager {
    private ModeState currentMode;

    public ModeManager() {
        currentMode = new ToolMode(this);
    }

    public Action parseInput(Input input, ViewPort viewPort) {
        return currentMode.parseInput(input, viewPort);
    }

    public String getName() {
        return currentMode.getName();
    }

    public void setMode(ModeState mode) {
        currentMode = mode;
    }
}
