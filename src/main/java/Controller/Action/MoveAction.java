package Controller.Action;

import Controller.ViewPort;
import Debugging.DebugLogger;
import FileStorage.TextFile;
import Shared.Direction;

public class MoveAction implements Action {

    private final Direction direction;

    public MoveAction(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        DebugLogger.info("ACTIONS", "Moving cursor in direction " + direction);
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
