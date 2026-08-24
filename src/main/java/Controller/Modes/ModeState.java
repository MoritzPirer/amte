package Controller.Modes;

import Controller.Action.Action;
import Controller.Input;
import Controller.ViewPort;

public interface ModeState {
    Action parseInput(Input input, ViewPort viewPort);
    String getName();
}
