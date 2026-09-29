package Controller.Modes;

import Controller.Action.Action;
import Controller.Input;
import Controller.ViewPortManager.ViewPort;
import Debugging.DebugLogger;
import FileStorage.TextFile;

public class ModeManager {
    private ModeState currentMode;
    private String lastFindTarget = null;

    public ModeManager() {
        currentMode = new ToolMode(this);
    }

    public Action parseInput(Input input, ViewPort viewPort, TextFile file) {
        DebugLogger.info("PARSING", "Parsing input " + input + " in mode " + currentMode.getName());
        return currentMode.parseInput(input, viewPort, file);
    }

    public String getName() {
        return currentMode.getName();
    }

    public void setMode(ModeState mode) {
        currentMode = mode;
    }

    public String getLastFindTarget() {
        return lastFindTarget;
    }

    public void setLastFindTarget(String lastFindTarget) {
        this.lastFindTarget = lastFindTarget;
    }

}
