package Controller.Action;

import Controller.ViewPort;
import FileStorage.FileIO.DiskFileLoader;
import FileStorage.FileIO.FileLoader;
import FileStorage.TextFile;

public class SaveAction implements Action {
    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        file.save();
        //TODO WHEN [temporary messages are implemented] DO [add a message confirming success / fail (check via file.isSaved())]
    }

    @Override
    public boolean canBeUndone() {
        return false;
    }

    @Override
    public void undo(ViewPort viewPort, TextFile file) {
        throw new UnsupportedOperationException("Not undoable! Did you call canBeUndone()?");
    }
}
