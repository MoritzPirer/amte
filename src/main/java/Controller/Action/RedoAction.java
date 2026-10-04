package Controller.Action;

import Controller.ViewPortManager.ViewPortManager;

/**
 * re-applies the last action to be undone
 */
public class RedoAction extends Action {

    @Override
    public void apply(ViewPortManager viewPortManager) {
        viewPortManager
            .current()
            .getHistory()
            .redo(viewPortManager);
    }
}
