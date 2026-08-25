package Controller.Action;

import Controller.ViewPort;
import FileStorage.TextFile;

public class QuitAction implements Action {

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        viewPort.stop();
    }

    @Override
    public boolean canBeUndone() {
        return false;
    }

    @Override
    public void undo(ViewPort viewPort, TextFile file) {
        throw new UnsupportedOperationException("Not undoable! Did you call canBeUndone() first?");
    }
}
