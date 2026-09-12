package Controller.History;

import Controller.Action.Action;
import Controller.ViewPort;
import FileStorage.TextFile;

import java.util.Stack;

public class StackHistory implements History {
    private final Stack<Action> undoableActions = new Stack<>();
    private final Stack<Action> redoableActions = new Stack<>();
    private boolean isMergePossible = false;

    @Override
    public void add(Action action) {
        if (!action.canBeUndone()) {
            isMergePossible = false;
            return;
        }

        if (shouldMerge(action)) {
            undoableActions.peek().absorb(action);
        }
        else {
            isMergePossible = true;
            undoableActions.push(action);
            redoableActions.clear();
        }
    }

    private boolean shouldMerge(Action action) {
        return isMergePossible &&  !undoableActions.isEmpty() && undoableActions.peek().canAbsorb(action);
    }

    @Override
    public void undo(ViewPort viewPort, TextFile textFile) {
        if (undoableActions.isEmpty()) {
            //TODO Language file
            viewPort.addTemporaryMessage("Nothing to undo.");
            return;
        }

        Action action = undoableActions.pop();
        action.undo(viewPort, textFile);

        redoableActions.push(action);
    }

    @Override
    public void redo(ViewPort viewPort, TextFile textFile) {
        if (redoableActions.isEmpty()) {
            //TODO Language file
            viewPort.addTemporaryMessage("Nothing to redo.");
            return;
        }

        Action action = redoableActions.pop();
        action.apply(viewPort, textFile);

        undoableActions.push(action);
    }
}
