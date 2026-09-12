package Controller.Action;

import Controller.ViewPort;
import Debugging.DebugLogger;
import FileStorage.TextFile;
import LanguageSupport.LanguageProvider;

public class QuitAction extends Action {
    private final boolean abortOnUnsavedChanges;

    private QuitAction(boolean abortOnUnsavedChanges) {
        this.abortOnUnsavedChanges = abortOnUnsavedChanges;
    }

    public static QuitAction makeSoftQuit() {
        return new QuitAction(true);
    }

    public static QuitAction makeForceQuit() {
        return new QuitAction(false);
    }


    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        if (abortOnUnsavedChanges) {
            DebugLogger.info("ACTIONS", "attempting soft quit");
            if (file.isSaved()) {
                viewPort.stop();
            }
            else {
                DebugLogger.info("ACTIONS", "aborting soft quit due to unsaved changes");
                LanguageProvider provider = new LanguageProvider();
                viewPort.addTemporaryMessage(provider.get("softQuitAbort"));
            }
        }
        else {
            DebugLogger.info("ACTIONS", "force quitting");
            viewPort.stop();
        }
    }
}
