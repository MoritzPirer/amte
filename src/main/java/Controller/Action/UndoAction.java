package Controller.Action;

import Controller.ViewPortManager.ViewPortManager;

/**
 * undoes the last action that is marked as undoable (via canBeUndone())
 */
public class UndoAction extends Action {
    @Override
    public void apply(ViewPortManager viewPortManager) {
        viewPortManager
            .current()
            .getHistory()
            .undo(viewPortManager);
    }
}
