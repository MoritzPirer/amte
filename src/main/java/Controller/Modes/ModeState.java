package Controller.Modes;

import Controller.Action.Action;
import Controller.Input;
import Controller.ViewPort;
import FileStorage.TextFile;

public interface ModeState {
    /**
     * converts the given input into an Action. If the input is unknown or if the input is part of
     * an unfinished multi-input command, a NullAction can be returned.
     * @param input the input to process
     * @param viewPort the viewport the resulting action applies to (e.g. to manipulate the cursor)
     * @param file the file the resulting action applies to (e.g. to manipulate file contents)
     * @return an Action that represents the intent of the input
     */
    Action parseInput(Input input, ViewPort viewPort, TextFile file);

    /**
     * @return the name of the mode to be displayed in the ui panel
     */
    String getName();
}
