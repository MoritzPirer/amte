package Controller.Action;

import Controller.ViewPort;
import FileStorage.TextFile;

/**
 * Outlines an action to be applied to a viewport
 */
public interface Action {
    /**
     * executes the action by modifying the given viewport.
     * @param viewPort the viewport the action will be applied to
     */
    void apply(ViewPort viewPort, TextFile file);

    /**
     * decides if the action is undoable. Should always be called before calling undo()
     * @return true if the action can be undone, false if not
     */
    boolean canBeUndone();

    /**
     * undoes the given action, such that calling apply(x) and undo(x) without any modifications to x in between
     * returns x to its original state. Some implementations are not undoable. calling undo() before apply(), or calling undo()
     * on a viewport that is not in the state it was immediately after apply() can have unpredictable effects or throw exceptions
     * @param viewPort the viewport that was originally modified
     * @throws UnsupportedOperationException if the implementation cannot be undone. Such implementations will have canBeUndone() returning false
     */
    void undo(ViewPort viewPort, TextFile file);
}
