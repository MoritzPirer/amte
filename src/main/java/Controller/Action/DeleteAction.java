package Controller.Action;


import Controller.Action.Ranges.FixedRangeResolver;
import Controller.Action.Ranges.Range;
import Controller.Action.Ranges.RangeResolver;
import Controller.ViewPort;
import Debugging.DebugLogger;
import FileStorage.TextFile;
import Shared.TextPosition;

public class DeleteAction extends Action {
    private String content = null;
    private final RangeResolver resolver;

    private TextPosition deletionStart = null;
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

        Range range = resolver.resolve(viewPort, file);
        DebugLogger.info("ACTIONS", "Delete Range evaluates to " + range);

        if (range.start().equals(range.end())) {
            DebugLogger.info("ACTIONS", "Delete canceled because range is empty delete");
            return;
        }

        deletionStart = range.start();
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

        file.insert(content, deletionStart);
        viewPort.moveCursorTo(restoreCursorTo);
    }

    @Override
    public boolean canAbsorb(Action action) {
        return false; //change to true when absorbing is implemented
    }

    @Override
    public void absorb(Action action) {
        throw new UnsupportedOperationException("TODO");
    }
}
