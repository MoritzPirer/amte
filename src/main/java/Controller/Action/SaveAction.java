package Controller.Action;

import Controller.TemporaryMessages.Message;
import Controller.TemporaryMessages.MessageType;
import Controller.TemporaryMessages.TimeDuration;
import Controller.ViewPortManager.ViewPort;
import Debugging.DebugLogger;
import FileStorage.TextFile;
import LanguageSupport.LanguageProvider;

public class SaveAction extends Action {

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        DebugLogger.info("ACTIONS", "saving file");
        file.save();

        LanguageProvider provider = new LanguageProvider();
        Message message;
        if (file.isSaved()) {
            DebugLogger.info("ACTIONS", "finished saving file");
            message = new Message(
                provider.get("command/save/success"),
                MessageType.INFO,
                TimeDuration.seconds(2)
            );
        }
        else {
            DebugLogger.warn("ACTIONS", "saving file failed");
            message = new Message(
                provider.get("command/save/failed"),
                MessageType.ERROR,
                TimeDuration.seconds(10)
            );
        }
        viewPort.addTemporaryMessage(message);
    }
}
