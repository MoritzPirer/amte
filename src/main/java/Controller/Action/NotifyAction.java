package Controller.Action;

import Controller.TemporaryMessages.Message;
import Controller.ViewPort;
import Debugging.DebugLogger;
import FileStorage.TextFile;

public class NotifyAction extends Action {
    private final Message message;

    public NotifyAction(Message message) {
        this.message = message;
    }

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        DebugLogger.info("ACTIONS", "adding temporary message with content " + message);
        viewPort.addTemporaryMessage(message);
    }
}
