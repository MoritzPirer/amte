package Controller.Action;

import Controller.ViewPort;
import Debugging.DebugLogger;
import FileStorage.TextFile;

public class NotifyAction extends Action {
    private final String message;

    public NotifyAction(String message) {
        this.message = message;
    }

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        DebugLogger.info("ACTIONS", "adding temporary message with content " + message);
        viewPort.addTemporaryMessage(message);
    }
}
