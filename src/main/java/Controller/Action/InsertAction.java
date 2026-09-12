package Controller.Action;

import Controller.ViewPort;
import Debugging.DebugLogger;
import FileStorage.TextFile;
import Shared.TextPosition;
import Shared.Utility.StringUtility;

public class InsertAction extends Action {
    private final String content;
    private final TextPosition position;

    public InsertAction(String content, TextPosition position) {
        this.content = content;
        this.position = position;
    }

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        DebugLogger.info("ACTIONS", "inserting content '" + content + "' at position " + position );

        file.insert(content, position);
        DebugLogger.info("ACTIONS",  "paragraph is now '" + file.readParagraph(position.paragraph()));
        TextPosition firstAfterInsert = findPositionAfterInsert();
        DebugLogger.info("DEBUG", "moving to " + firstAfterInsert);
        viewPort.moveCursorTo(firstAfterInsert);
    }

    private TextPosition findPositionAfterInsert() {
        if (content.isEmpty()) {
            return position;
        }

        int paragraphsAdded = StringUtility.countOccurrences(content, '\n');

        if (paragraphsAdded == 0) {
            return position.offset(0, content.length());
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

    @Override
    public boolean canAbsorb(Action action) {
        return false;
    }

    @Override
    public void absorb(Action action) {
        throw new UnsupportedOperationException("TODO");
    }
}
