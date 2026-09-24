package Controller.Action;

import Controller.ViewPort;
import Debugging.DebugLogger;
import FileStorage.FileIO.DiskFileLoader;
import FileStorage.FileIO.FileLoader;
import FileStorage.TextFile;
import LanguageSupport.LanguageProvider;

public class SaveAction extends Action {

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        DebugLogger.info("ACTIONS", "saving file");
        file.save();

        LanguageProvider provider = new LanguageProvider();
        if (file.isSaved()) {
            DebugLogger.info("ACTIONS", "finished saving file");
            viewPort.addTemporaryMessage(provider.get("command/save/success"));
        }
        else {
            DebugLogger.warn("ACTIONS", "saving file failed");
            viewPort.addTemporaryMessage(provider.get("command/save/failed"));
        }
    }
}
