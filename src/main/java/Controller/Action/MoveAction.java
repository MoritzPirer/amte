package Controller.Action;

import Controller.ViewPortManager.ViewPortManager;
import Debugging.DebugLogger;
import Shared.Direction;

public class MoveAction extends Action {

    private final Direction direction;

    public MoveAction(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void apply(ViewPortManager viewPortManager) {
        DebugLogger.info("ACTIONS", "Moving cursor in direction " + direction);
        viewPortManager.current().moveCursor(direction);
    }
}
