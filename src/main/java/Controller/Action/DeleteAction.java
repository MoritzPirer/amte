package Controller.Action;


import Controller.ViewPort;
import Shared.TextPosition;

public class DeleteAction implements Action {
    private String content = null;
    private final TextPosition start;
    private final TextPosition end;

    public DeleteAction(TextPosition start, TextPosition end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public void apply(ViewPort viewPort) {
        content = viewPort.readRange(start, end);
        viewPort.deleteRange(start, end);

        viewPort.moveCursorTo(start);
    }

    @Override
    public boolean canBeUndone() {
        return true;
    }

    @Override
    public void undo(ViewPort viewPort) {
        assert content != null;
        //TODO WHEN [adding undo and redo functionality] DO [implement this function]
    }
}
