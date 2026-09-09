package Controller.Action;

import Controller.Action.Ranges.Range;
import Controller.Action.Ranges.RangeResolver;
import Controller.ViewPort;
import FileStorage.TextFile;
import Shared.TextPosition;

public class JumpAction implements Action {

    private final RangeResolver resolver;
    private final boolean jumpToStart;

    /**
     * Moves the Cursor to one end of the range
     * @param resolver A resolver that can generate a Range appropriate for the user tool
     * @param jumpToStart if true, use the start of the resolved range as the destination, otherwise use the end.
     */
    public JumpAction(RangeResolver resolver, boolean jumpToStart) {
        this.resolver = resolver;
        this.jumpToStart = jumpToStart;
    }

    public JumpAction(RangeResolver resolver) {
        this(resolver, false);
    }

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        Range range = resolver.resolve(viewPort, file);
        if (jumpToStart) {
            viewPort.moveCursorTo(range.start());
        }
        else {
            viewPort.moveCursorTo(range.end());
        }
    }

    @Override
    public boolean canBeUndone() {
        return false;
    }

    @Override
    public void undo(ViewPort viewPort, TextFile file) {
        throw new UnsupportedOperationException("Not undoable! Did you call canBeUndone()?");
    }
}
