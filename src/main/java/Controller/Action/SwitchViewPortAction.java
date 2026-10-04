package Controller.Action;

import Controller.ViewPortManager.ViewPortManager;
import Shared.Direction;

/**
 * changes the active viewport
 */
public class SwitchViewPortAction extends Action {
    Direction direction;

    public SwitchViewPortAction(Direction direction) {
        if (direction == Direction.UP || direction == Direction.DOWN) {
            throw new IllegalArgumentException("Invalid direction");
        }

        this.direction = direction;
    }

    @Override
    public void apply(ViewPortManager viewPortManager) {
        if (direction == Direction.LEFT) {
            viewPortManager.previous();
        }
        else {
            viewPortManager.next();
        }
    }
}
