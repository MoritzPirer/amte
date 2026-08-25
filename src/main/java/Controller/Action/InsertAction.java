package Controller.Action;

import Controller.ViewPort;
import FileStorage.TextFile;
import Shared.TextPosition;
import Shared.Utility.StringUtility;

public class InsertAction implements Action {
    private final String content;
    private final TextPosition position;

    public InsertAction(String content, TextPosition position) {
        this.content = content;
        this.position = position;
    }

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        file.insert(content, viewPort.getLogicalCursorPosition());
        TextPosition firstAfterInsert = findPositionAfterInsert();
        viewPort.moveCursorTo(firstAfterInsert);
    }

    private TextPosition findPositionAfterInsert() {
        if (content.isEmpty()) {
            return position;
        }

        int paragraphsAdded = StringUtility.countOccurrences(content, '\n');

        if (paragraphsAdded == 0) {
            return new TextPosition(position.paragraph(), position.character() + content.length());
        }

        String lastLine = content.substring(content.lastIndexOf('\n') + 1);
        return new TextPosition(position.paragraph() + paragraphsAdded, lastLine.length());
    }

    @Override
    public boolean canBeUndone() {
        return true;
    }

    @Override
    public void undo(ViewPort viewPort, TextFile file) {
        //TODO WHEN [adding undo and redo functionality] DO [implement this function]
    }

}
