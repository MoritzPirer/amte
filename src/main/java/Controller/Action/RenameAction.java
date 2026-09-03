package Controller.Action;

import Controller.ViewPort;
import FileStorage.TextFile;

public class RenameAction implements Action {
    private String previousName;
    private String newName;

    public RenameAction(String newName) {
        this.newName = newName;
    }

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
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
    public void undo(ViewPort viewPort, TextFile file) {
        file.setFileName(previousName);
    }
}
