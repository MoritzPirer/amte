package Controller.Action.Ranges;

import Controller.ViewPort;
import FileStorage.TextFile;
import Shared.TextPosition;

public class CurrentParagraphRangeResolver implements RangeResolver {
    @Override
    public Range resolve(ViewPort viewPort, TextFile file) {
        TextPosition cursor = viewPort.getLogicalCursorPosition();
        int paragraph = cursor.paragraph();
        int lastCharacter = file.lengthOfParagraph(paragraph) - 1;

        TextPosition first = new TextPosition(paragraph, 0);
        TextPosition last = new TextPosition(paragraph, lastCharacter);

        return new Range(first, last);
    }
}
