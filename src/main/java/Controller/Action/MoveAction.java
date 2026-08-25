package Controller.Action;

import Controller.ViewPort;
import FileStorage.TextFile;
import Shared.Direction;

public class MoveAction implements Action {

    private final Direction direction;

    public MoveAction(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        viewPort.moveCursor(direction);
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
