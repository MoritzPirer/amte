package Controller.Action;


import Controller.Action.Ranges.FixedRangeResolver;
import Controller.Action.Ranges.Range;
import Controller.Action.Ranges.RangeResolver;
import Controller.ViewPort;
import Debugging.DebugLogger;
import FileStorage.TextFile;
import Shared.TextPosition;
import Shared.Utility.StringUtility;

public class DeleteAction extends Action {
    private String content = null;
    private final RangeResolver resolver;

    private Range range;
    private TextPosition restoreCursorTo = null;

    /**
     * Deletes the content within the resolved range, including start but not including end
     * (i.e. Range.end() is the first character after the deleted section)
     * @param resolver a RangeResolver that can evaluate to the appropriate start and end of the range
     */
    public DeleteAction(RangeResolver resolver) {
        this.resolver = resolver;
    }
    public DeleteAction(TextPosition start, TextPosition end) {
        this.resolver = new FixedRangeResolver(start, end);
    }

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        DebugLogger.info("ACTIONS", "applying DeleteAction");
        if (file.isEmpty()) {
            DebugLogger.info("ACTIONS", "Delete canceled because file is empty");
            return;
        }

        range = resolver.resolve(viewPort, file);
        DebugLogger.info("ACTIONS", "Delete Range evaluates to " + range);

        if (range.start().equals(range.end())) {
            DebugLogger.info("ACTIONS", "Delete canceled because range is empty delete");
            return;
        }

        restoreCursorTo = viewPort.getLogicalCursorPosition();
        content = file.readRange(range.start(), range.end());
        file.deleteRange(range.start(), range.end());

        viewPort.moveCursorTo(range.start());
    }

    @Override
    public boolean canBeUndone() {
        return true;
    }

    @Override
    public void undo(ViewPort viewPort, TextFile file) {
        if (content == null) {
            return; // valid state if apply() ended early (e.g. empty file)
        }

        file.insert(content, range.start());
        viewPort.moveCursorTo(restoreCursorTo);
    }

    @Override
    public boolean canAbsorb(Action action) {
        if (!(action instanceof DeleteAction deleteAction)) {
            DebugLogger.info("HISTORY", "not merging because new action is not DeleteAction");
            return false;
        }

        if (this.range == null || deleteAction.range == null) {
            DebugLogger.info("HISTORY", "not merging because one or both actions modified empty file");
            return false;
        }

        if (!areAdjacent(this.range, deleteAction.range)) {
            DebugLogger.info("HISTORY", "not merging because deletes are not adjacent");
            return false;
        }

        if (this.range.isEmpty() || deleteAction.range.isEmpty()) {
            DebugLogger.info("HISTORY", "not merging because one or both actions are empty");
            return false;
        }

        char endOfFirst;
        char startOfSecond;
        if (this.range.isBefore(deleteAction.range)) {
            endOfFirst = this.content.charAt(this.content.length() - 1);
            startOfSecond = deleteAction.content.charAt(0);
        }
        else {
            endOfFirst = deleteAction.content.charAt(deleteAction.content.length() - 1);
            startOfSecond = this.content.charAt(0);
        }

        if (!StringUtility.isAlphanumeric(endOfFirst) || !StringUtility.isAlphanumeric(startOfSecond)) {
            DebugLogger.info("HISTORY", "not merging because the merging point is the end of a word");
            return false;
        }

        return true;
    }

    private boolean areAdjacent(Range a, Range b) {
        if (a.end().equals(b.start())) {
            return true;
        }

        if (b.end().equals(a.start())) {
            return true;
        }

        return (a.start().equals(b.start()));
    }

    @Override
    public void absorb(Action action) {
        assert action instanceof DeleteAction;

        DeleteAction deleteAction = (DeleteAction) action;

        if (deleteAction.range.isBefore(this.range)) {
            absorbBefore(deleteAction);
        }
        else {
            absorbAfter(deleteAction);
        }
    }

    private void absorbAfter(DeleteAction action) {
       this.range = new Range(this.range.start(), action.range.end());
       this.content = this.content + action.content;
    }

    private void absorbBefore(DeleteAction action) {
        this.range = new Range(action.range.start(), this.range.end());
        this.content = action.content + this.content;
    }
}
