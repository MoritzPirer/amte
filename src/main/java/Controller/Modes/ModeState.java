package Controller.Modes;

import Controller.Action.Action;
import Controller.Input;
import Controller.ViewPort;
import FileStorage.TextFile;

public interface ModeState {
    Action parseInput(Input input, ViewPort viewPort, TextFile file);
    String getName();
}
