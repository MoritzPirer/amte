package Controller.Action;

import Controller.ViewPortManager.ViewPortManager;

public class UndoAction extends Action {
    @Override
    public void apply(ViewPortManager viewPortManager) {
        viewPortManager
            .current()
            .getHistory()
            .undo(viewPortManager);
    }
}
