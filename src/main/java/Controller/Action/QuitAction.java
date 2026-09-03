package Controller.Action;

import Controller.ViewPort;
import FileStorage.TextFile;

public class QuitAction implements Action {
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
            if (file.isSaved()) {
                viewPort.stop();
            }
            else {
                //TODO language file
                viewPort.addTemporaryMessage("you have unsaved changes!");
            }
        }
        else {
            viewPort.stop();
        }
    }

    @Override
    public boolean canBeUndone() {
        return false;
    }

    @Override
    public void undo(ViewPort viewPort, TextFile file) {
        throw new UnsupportedOperationException("Not undoable! Did you call canBeUndone() first?");
    }
}
