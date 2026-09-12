package Controller.Action;

import Controller.ViewPort;
import Debugging.DebugLogger;
import FileStorage.TextFile;
import Shared.Direction;

public class MoveAction extends Action {

    private final Direction direction;

    public MoveAction(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        DebugLogger.info("ACTIONS", "Moving cursor in direction " + direction);
        viewPort.moveCursor(direction);
    }
}
