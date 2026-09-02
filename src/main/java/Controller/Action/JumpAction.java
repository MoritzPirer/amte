package Controller.Action;

import Controller.ViewPort;
import FileStorage.TextFile;
import Shared.TextPosition;

public class JumpAction implements Action {

    private final TextPosition position;

    public JumpAction(TextPosition position) {
        this.position = position;
    }

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        viewPort.moveCursorTo(position);
    }

    @Override
    public boolean canBeUndone() {
        return false;
    }

    @Override
    public void undo(ViewPort viewPort, TextFile file) {
        throw new UnsupportedOperationException("Not undoable! Did you call canBeUndone()?");
    }
}
