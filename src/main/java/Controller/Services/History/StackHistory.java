package Controller.Services.History;

import Controller.Action.Action;
import Controller.TemporaryMessages.Message;
import Controller.TemporaryMessages.MessageType;
import Controller.TemporaryMessages.TimeDuration;
import Controller.ViewPortManager.ViewPortManager;
import Debugging.DebugLogger;
import LanguageSupport.LanguageProvider;

import java.util.Stack;

public class StackHistory implements History {
    private static final LanguageProvider provider = new LanguageProvider();

    private final Stack<Action> undoableActions = new Stack<>();
    private final Stack<Action> redoableActions = new Stack<>();
    private boolean isMergePossible = false;

    @Override
    public void add(Action action) {
        DebugLogger.info("HISTORY", action.getClass().getSimpleName());
        if (!action.canBeUndone()) {
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
    public void undo(ViewPortManager viewPortManager) {
        if (undoableActions.isEmpty()) {
            viewPortManager.current().addTemporaryMessage(new Message(
                provider.get("tool/history/nothingToUndo"),
                MessageType.ERROR,
                TimeDuration.seconds(2)
            ));
            return;
        }

        Action action = undoableActions.pop();
        action.undo(viewPortManager);

        redoableActions.push(action);
    }

    @Override
    public void redo(ViewPortManager viewPortManager) {
        if (redoableActions.isEmpty()) {
            viewPortManager.current().addTemporaryMessage(new Message(
                provider.get("tool/history/nothingToRedo"),
                MessageType.ERROR,
                TimeDuration.seconds(2)
            ));
            return;
        }

        Action action = redoableActions.pop();
        action.apply(viewPortManager);

        undoableActions.push(action);
    }
}
