package Controller.Action;

import Controller.Action.Ranges.Range;
import Controller.Action.Ranges.RangeResolver;
import Controller.ViewPort;
import Debugging.DebugLogger;
import FileStorage.TextFile;
import Shared.TextPosition;

public class JumpAction extends Action {

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
            DebugLogger.info("ACTIONS", "jumping cursor to " + range.start());
            viewPort.moveCursorTo(range.start());
        }
        else {
            DebugLogger.info("ACTIONS", "jumping cursor to " + range.end());
            viewPort.moveCursorTo(range.end());
        }
    }
}
