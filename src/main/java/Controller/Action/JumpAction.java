package Controller.Action;

import Controller.Action.Ranges.Range;
import Controller.Action.Ranges.RangeResolver;
import Controller.ViewPort;
import FileStorage.TextFile;
import Shared.TextPosition;

public class JumpAction implements Action {

    private final RangeResolver resolver;
    private final boolean jumpToStart;

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
            viewPort.moveCursorTo(file.previousPosition(range.end())); //end is exclusive -> prevent out of bounds
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
