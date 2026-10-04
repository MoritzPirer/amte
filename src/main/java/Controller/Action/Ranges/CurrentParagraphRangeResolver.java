package Controller.Action.Ranges;

import Controller.ViewPortManager.ViewPort;
import FileStorage.TextFile;
import Shared.TextPosition;

/**
 * Resolves to a range that includes the entire paragraph the cursor is in at the time of resolving.
 * the end of the returned value is the hypothetical position just after the last character of the paragraph
 */
public class CurrentParagraphRangeResolver implements RangeResolver {
    @Override
    public Range resolve(ViewPort viewPort, TextFile file) {
        if (file.isEmpty()) {
            return new Range(new TextPosition(0, 0), new TextPosition(0, 1));
        }

        TextPosition cursor = viewPort.getLogicalCursorPosition();
        int paragraph = cursor.paragraph();
        int lastCharacter = file.lengthOfParagraph(paragraph);

        TextPosition first = new TextPosition(paragraph, 0);
        TextPosition last = new TextPosition(paragraph, lastCharacter);

        return new Range(first, last);
    }
}
