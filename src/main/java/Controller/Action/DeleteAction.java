package Controller.Action;


import Controller.ViewPort;
import FileStorage.TextFile;
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
    public void apply(ViewPort viewPort, TextFile file) {
        System.out.println("Delete Action with start: " + start + " and end: " + end);
        content = file.readRange(start, end);
        file.deleteRange(start, end);

        viewPort.moveCursorTo(start);
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
