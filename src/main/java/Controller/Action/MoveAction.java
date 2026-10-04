package Controller.Action;

import Controller.ViewPortManager.ViewPortManager;
import Debugging.DebugLogger;
import Shared.Direction;

/**
 * move the cursor one character to the side or one paragraph up or down
 */
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
