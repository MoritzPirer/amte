package Controller.Action;

import Controller.ViewPortManager.ViewPortManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Allows for multiple actions to be treated as a single action to allow for composing of more complicated modifications
 */
public class CompoundAction extends Action {
    private final List<Action> actions;

    public CompoundAction(Action... actions) {
        this.actions = Arrays.asList(actions);
    }
    public CompoundAction(Collection<Action> actions) {
        this.actions = actions.stream().toList();
    }

    @Override
    public void apply(ViewPortManager viewPortManager) {
        for (Action action : actions) {
            action.apply(viewPortManager);
        }
    }

    @Override
    public boolean canBeUndone() {
        for (Action action : actions) {
            if (action.canBeUndone()) {
                return true;
            }
        }

        return true;
    }

    @Override
    public void undo(ViewPortManager viewPortManager) {
        //iterate in reverse for stack-like behavior, i.e. last to be applied must be undone first
        for (int i = actions.size() - 1; i >= 0; i--) {
            Action action = actions.get(i);

            if (action.canBeUndone()) {
                action.undo(viewPortManager);
            }
        }
    }
}
