package Controller.Action;

import Controller.View.Line;
import Controller.ViewPort;
import FileStorage.TextFile;
import Shared.TextPosition;
import Shared.Utility.StringUtility;

public class ParagraphJoiningAction implements Action {
    private final int upperParagraph;

    public ParagraphJoiningAction(int upperParagraph) {
        this.upperParagraph = upperParagraph;
    }


    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        if (upperParagraph >= file.length() - 1) {
            return;
        }

        String first = file.readParagraph(upperParagraph).toString();
        String second = file.readParagraph(upperParagraph + 1).toString();

        int firstTrailingWhiteSpace = StringUtility.countTrailingWhiteSpace(first);
        int secondLeadingWhiteSpace = StringUtility.countLeadingWhiteSpace(second);

        //TODO WHEN [undo is being implemented] do [store what whitespace was removed to restore it]
        first = first.substring(0, first.length() - firstTrailingWhiteSpace);
        second = second.substring(secondLeadingWhiteSpace);

        String combined = first + " " + second;

        TextPosition start = new TextPosition(upperParagraph, 0);
        file.deleteRange(start, new TextPosition(upperParagraph + 2, 0));
        file.insert(combined, start);
    }

    @Override
    public boolean canBeUndone() {
        return true;
    }

    @Override
    public void undo(ViewPort viewPort, TextFile file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
