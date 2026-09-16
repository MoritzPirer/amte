package Controller.Action;

import Controller.ViewPort;
import Debugging.DebugLogger;
import FileStorage.TextFile;
import Shared.TextPosition;
import Shared.Utility.StringUtility;

import java.util.Arrays;

public class InsertAction extends Action {
    private String content;
    private TextPosition position;
    private TextPosition firstAfterInsert = null;

    public InsertAction(String content, TextPosition position) {
        this.content = content;
        this.position = position;
    }

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        DebugLogger.info("ACTIONS", "inserting content '" + content + "' at position " + position );

        file.insert(content, position);
        firstAfterInsert = findPositionAfterInsert();
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

        if (content.endsWith("\n")) {
            String[] lines = content.split("\n");

            if (lines.length == 0) {
                return new TextPosition(position.paragraph() + paragraphsAdded, 0);
            }
            return new TextPosition(position.paragraph() + paragraphsAdded - 1, lines[lines.length - 1].length());
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
        file.deleteRange(position, firstAfterInsert);
        viewPort.moveCursorTo(position);
    }

    @Override
    public boolean canAbsorb(Action action) {
        if (!(action instanceof InsertAction insertAction)) {
            DebugLogger.info("HISTORY", "not merging because new action is not InsertAction");
            return false;
        }

        if (content.isEmpty() || insertAction.content.isEmpty()) {
            DebugLogger.info("HISTORY", "not merging because one or both actions do not have content");
            return false;
        }

        //THIS CHECK IS WRONG. SHOULD check if a.position is directly after b. firstafterinsert
        if (!this.position.equals(insertAction.firstAfterInsert) && !insertAction.position.equals(this.firstAfterInsert)) {
            DebugLogger.info("HISTORY", "not merging because inserts are not adjacent");
            DebugLogger.warn("HISTORY", "this.position" + this.position + " this.firstAfterInsert" + this.firstAfterInsert);
            DebugLogger.warn("HISTORY", "other.position" + insertAction.position + " other.firstAfterInsert" + insertAction.firstAfterInsert);
            return false;
        }

        char endOfFirst;
        char startOfSecond;

        if (this.position.isBefore(insertAction.position)) {
            endOfFirst = this.content.charAt(this.content.length() - 1);
            startOfSecond = insertAction.content.charAt(0);
        }
        else {
            endOfFirst = insertAction.content.charAt(insertAction.content.length() - 1);
            startOfSecond = this.content.charAt(0);
        }

        if (!StringUtility.isAlphanumeric(endOfFirst) || !StringUtility.isAlphanumeric(startOfSecond)) {
            DebugLogger.info("HISTORY", "not merging because the merging point is the end of a word");
            return false;
        }

        return true;
    }

    @Override
    public void absorb(Action action) {
        assert action instanceof InsertAction;

        InsertAction insertAction = (InsertAction) action;

        if (insertAction.position.isBefore(this.position)) {
            absorbBefore(insertAction);
        }
        else {
            absorbAfter(insertAction);
        }
    }

    private void absorbBefore(InsertAction insertAction) {
        this.position = insertAction.position;
        this.content = insertAction.content + this.content;
    }

    private void absorbAfter(InsertAction insertAction) {
        this.content += insertAction.content;
        this.firstAfterInsert = insertAction.firstAfterInsert;
    }
}
