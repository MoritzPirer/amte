package Controller.Action;


import Controller.Action.Ranges.FixedRangeResolver;
import Controller.Action.Ranges.Range;
import Controller.Action.Ranges.RangeResolver;
import Controller.ViewPort;
import FileStorage.TextFile;
import Shared.TextPosition;

public class DeleteAction implements Action {
    private String content = null;
    private final RangeResolver resolver;

    /**
     * Deletes the content within the resolved range, including start but not including end
     * (i.e. Range.end() is the first character after the deleted section)
     * @param resolver
     */
    public DeleteAction(RangeResolver resolver) {
        this.resolver = resolver;
    }
    public DeleteAction(TextPosition start, TextPosition end) {
        this.resolver = new FixedRangeResolver(start, end);
    }

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        if (file.isEmpty()) {
            return;
        }

        Range range = resolver.resolve(viewPort, file);
        if (range.start().equals(range.end())) {
            return;
        }
        System.out.println("Deleting " + range.start() + " -> " + range.end());

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
        assert content != null;
        //TODO WHEN [adding undo and redo functionality] DO [implement this function]
    }
}
