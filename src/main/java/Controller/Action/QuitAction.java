package Controller.Action;

import Controller.ViewPort;

public class QuitAction implements Action {

    @Override
    public void apply(ViewPort viewPort) {
        viewPort.stop();
    }

    @Override
    public boolean canBeUndone() {
        return false;
    }

    @Override
    public void undo(ViewPort viewPort) {
        throw new UnsupportedOperationException("Not undoable! Did you call canBeUndone() first?");
    }
}
