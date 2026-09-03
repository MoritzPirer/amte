package Controller.Action;

import Controller.ViewPort;
import FileStorage.TextFile;

public class NotifyAction implements Action {
    private final String message;

    public NotifyAction(String message) {
        this.message = message;
    }

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        viewPort.addTemporaryMessage(message);
    }

    @Override
    public boolean canBeUndone() {
        return false;
    }

    @Override
    public void undo(ViewPort viewPort, TextFile file) {
        throw new UnsupportedOperationException("Not undoable!");
    }
}
