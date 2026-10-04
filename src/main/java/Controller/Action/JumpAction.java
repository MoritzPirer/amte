package Controller.Action;

import Controller.Action.Ranges.FixedRangeResolver;
import Controller.Action.Ranges.Range;
import Controller.Action.Ranges.RangeResolver;
import Controller.ViewPortManager.ViewPort;
import Controller.ViewPortManager.ViewPortManager;
import Debugging.DebugLogger;
import Shared.TextPosition;

/**
 * jump to specified position in the file
 */
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

    public JumpAction(TextPosition position) {
        this.resolver = new FixedRangeResolver(null, position);
        this.jumpToStart = false;
    }

    @Override
    public void apply(ViewPortManager viewPortManager) {
        ViewPort viewPort = viewPortManager.current();
        Range range = resolver.resolve(viewPort, viewPort.getFile());

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
