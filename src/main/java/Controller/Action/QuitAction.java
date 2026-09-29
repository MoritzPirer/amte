package Controller.Action;

import Controller.TemporaryMessages.InputCountDuration;
import Controller.TemporaryMessages.Message;
import Controller.TemporaryMessages.MessageType;
import Controller.ViewPortManager.ViewPort;
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
            softQuit(viewPort, file);
        }
        else {
            DebugLogger.info("ACTIONS", "force quitting");
            viewPort.stop();
        }
    }

    private void softQuit(ViewPort viewPort, TextFile file) {
        DebugLogger.info("ACTIONS", "attempting soft quit");

        if (file.isSaved()) {
            viewPort.stop();
        }
        else {
            DebugLogger.info("ACTIONS", "aborting soft quit due to unsaved changes");
            LanguageProvider provider = new LanguageProvider();
            Message message = new Message(
                provider.get("command/quit/abort"),
                MessageType.ERROR,
                new InputCountDuration(1)
            );
            viewPort.addTemporaryMessage(message);
        }
    }
}
