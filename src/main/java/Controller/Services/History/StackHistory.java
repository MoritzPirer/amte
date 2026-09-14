package Controller.Services.History;

import Controller.Action.Action;
import Controller.ViewPort;
import Debugging.DebugLogger;
import FileStorage.TextFile;
import LanguageSupport.LanguageProvider;

import java.util.Stack;

public class StackHistory implements History {
    private static final LanguageProvider provider = new LanguageProvider();

    private final Stack<Action> undoableActions = new Stack<>();
    private final Stack<Action> redoableActions = new Stack<>();
    private boolean isMergePossible = false;

    @Override
    public void add(Action action) {
        if (!action.canBeUndone()) {
            DebugLogger.warn("HISTORY", action.getClass().getSimpleName());
            DebugLogger.info("HISTORY", "action not added to history because it is not undoable");
            isMergePossible = false;
            return;
        }

        if (shouldMerge(action)) {
            DebugLogger.info("HISTORY", "action absorbed by previous action");
            undoableActions.peek().absorb(action);
        }
        else {
            DebugLogger.info("HISTORY", "action added to history");
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
            viewPort.addTemporaryMessage(provider.get("nothingToRedo"));
            return;
        }

        Action action = undoableActions.pop();
        action.undo(viewPort, textFile);

        redoableActions.push(action);
    }

    @Override
    public void redo(ViewPort viewPort, TextFile textFile) {
        if (redoableActions.isEmpty()) {
            viewPort.addTemporaryMessage(provider.get("nothingToUndo"));
            return;
        }

        Action action = redoableActions.pop();
        action.apply(viewPort, textFile);

        undoableActions.push(action);
    }
}
