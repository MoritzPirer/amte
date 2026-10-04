package Controller.Action;

import Controller.TemporaryMessages.Message;
import Controller.ViewPortManager.ViewPortManager;
import Debugging.DebugLogger;

/**
 * Display a message to the user
 */
public class NotifyAction extends Action {
    private final Message message;

    public NotifyAction(Message message) {
        this.message = message;
    }

    @Override
    public void apply(ViewPortManager viewPortManager) {
        DebugLogger.info("ACTIONS", "adding temporary message with content " + message);
        viewPortManager.current().addTemporaryMessage(message);
    }
}
