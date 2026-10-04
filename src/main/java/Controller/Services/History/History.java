package Controller.Services.History;

import Controller.Action.Action;
import Controller.ViewPortManager.ViewPort;
import Controller.ViewPortManager.ViewPortManager;
import FileStorage.TextFile;

public interface History {

    /**
     * Add an action into the history. Can safely be called with non-undoable actions, these will be ignored by undo()
     * and redo()
     * @param action the action to add
     */
    void add(Action action);

    /**
     * undo the most recently added undoable action
     * @param viewPortManager the viewPortManager the action was applied to
     */
    void undo(ViewPortManager viewPortManager);

    /**
     * re-apply the most recently undone action. If no eligible action exists, the user is notified
     * @param viewPortManager the viewPort to apple the action to
     */
    void redo(ViewPortManager viewPortManager);
}
