package Controller.Action;

import Controller.ViewPort;
import FileStorage.TextFile;

public class RedoAction extends Action {

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        viewPort.getHistory().redo(viewPort, file);
    }
}
