package Controller.Action;

import Controller.ViewPort;
import FileStorage.TextFile;

public class UndoAction extends Action {
    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        viewPort.getHistory().undo(viewPort, file);
    }
}
