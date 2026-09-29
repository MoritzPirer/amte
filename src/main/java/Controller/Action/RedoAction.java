package Controller.Action;

import Controller.ViewPortManager.ViewPortManager;

public class RedoAction extends Action {

    @Override
    public void apply(ViewPortManager viewPortManager) {
        viewPortManager
            .current()
            .getHistory()
            .redo(viewPortManager);
    }
}
