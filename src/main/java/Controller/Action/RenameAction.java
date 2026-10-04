package Controller.Action;

import Controller.ViewPortManager.ViewPortManager;
import Debugging.DebugLogger;
import FileStorage.TextFile;

/**
 * Renames the file associated with the active viewport
 */
public class RenameAction extends Action {
    private String previousName;
    private String newName;

    public RenameAction(String newName) {
        this.newName = newName;
    }

    @Override
    public void apply(ViewPortManager viewPortManager) {
        DebugLogger.info("ACTIONS", "renaming file to name " + newName);

        TextFile file = viewPortManager.current().getFile();

        if (!newName.contains(".")) {
            String fileExtension = file.getFileName().substring(file.getFileName().lastIndexOf("."));
            newName += fileExtension;
        }

        previousName = file.getFileName();
        file.setFileName(newName);
    }

    @Override
    public boolean canBeUndone() {
        return true;
    }

    @Override
    public void undo(ViewPortManager viewPortManager) {
        viewPortManager
            .current()
            .getFile()
            .setFileName(previousName);
    }
}
