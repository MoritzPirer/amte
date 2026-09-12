package Controller.Action;

import Controller.ViewPort;
import FileStorage.TextFile;

import java.util.Arrays;
import java.util.List;

public class CompoundAction extends Action {
    private final List<Action> actions;

    public CompoundAction(Action... actions) {
        this.actions = Arrays.asList(actions);
    }
    public CompoundAction(List<Action> actions) {
        this.actions = actions;
    }

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        for (Action action : actions) {
            action.apply(viewPort, file);
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
    public void undo(ViewPort viewPort, TextFile file) {
        for (Action action : actions) {
            if (action.canBeUndone()) {
                action.undo(viewPort, file);
            }
        }
    }
}
