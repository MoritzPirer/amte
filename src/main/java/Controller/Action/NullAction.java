package Controller.Action;

import Controller.ViewPort;
import FileStorage.TextFile;

public class NullAction extends Action {
    @Override
    public void apply(ViewPort viewPort, TextFile file) { }
}
