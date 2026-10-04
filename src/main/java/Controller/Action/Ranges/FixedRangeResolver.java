package Controller.Action.Ranges;

import Controller.ViewPortManager.ViewPort;
import FileStorage.TextFile;
import Shared.TextPosition;

/**
  Resolved to a specific range defined at construction
 */
public class FixedRangeResolver implements RangeResolver {

    private final Range range;

    public FixedRangeResolver(TextPosition start, TextPosition end) {
        this.range = new Range(start, end);
    }
    @Override
    public Range resolve(ViewPort viewPort, TextFile file) {
        return range;
    }
}
