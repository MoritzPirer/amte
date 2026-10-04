package Controller.Action;

import Controller.TemporaryMessages.InputCountDuration;
import Controller.TemporaryMessages.Message;
import Controller.TemporaryMessages.MessageType;
import Controller.ViewPortManager.ViewPort;
import Controller.ViewPortManager.ViewPortManager;
import Debugging.DebugLogger;
import LanguageSupport.LanguageProvider;

/**
 * Closes the active viewPort
 */
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
    public void apply(ViewPortManager viewPortManager) {
        if (abortOnUnsavedChanges) {
            softQuit(viewPortManager);
        }
        else {
            DebugLogger.info("ACTIONS", "force quitting");
            viewPortManager.closeCurrent();
        }
    }

    private void softQuit(ViewPortManager viewPortManager) {
       ViewPort viewPort = viewPortManager.current();

        DebugLogger.info("ACTIONS", "attempting soft quit");

        if (viewPort.getFile().isSaved()) {
            viewPortManager.closeCurrent();
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
