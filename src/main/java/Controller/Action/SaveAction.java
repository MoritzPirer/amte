package Controller.Action;

import Controller.ViewPort;
import FileStorage.FileIO.DiskFileLoader;
import FileStorage.FileIO.FileLoader;
import FileStorage.TextFile;
import LanguageSupport.LanguageProvider;

public class SaveAction implements Action {
    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        file.save();
        LanguageProvider provider = new LanguageProvider();
        if (file.isSaved()) {
            viewPort.addTemporaryMessage(provider.get("saveSuccess"));
        }
        else {
            viewPort.addTemporaryMessage(provider.get("saveFail"));
        }
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
