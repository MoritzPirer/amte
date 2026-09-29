package Controller.Action.Ranges;

import Controller.ViewPortManager.ViewPort;
import FileStorage.TextFile;
import Shared.TextPosition;

public class RestOfParagraphRangeResolver implements RangeResolver {
    @Override
    public Range resolve(ViewPort viewPort, TextFile file) {
        TextPosition cursor = viewPort.getLogicalCursorPosition();
        int paragraph = cursor.paragraph();
        int lastCharacter = file.lengthOfParagraph(paragraph) - 1;

        TextPosition last = new TextPosition(paragraph, lastCharacter);

        return new Range(cursor, last);
    }
}
