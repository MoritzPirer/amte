package Controller.Action;

import Controller.View.Line;
import Controller.ViewPort;
import FileStorage.TextFile;
import Shared.TextPosition;
import Shared.Utility.StringUtility;

public class ParagraphJoiningAction extends Action {
    private final int upperParagraph;

    private String removedFromFirst;
    private String removedFromSecond;
    private int joinIndex;

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

        removedFromFirst = first.substring(first.length() - firstTrailingWhiteSpace);
        first = first.substring(0, first.length() - firstTrailingWhiteSpace);

        removedFromSecond = second.substring(0, secondLeadingWhiteSpace);
        second = second.substring(secondLeadingWhiteSpace);

        String combined = first + " " + second;
        joinIndex = first.length();

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
        String joined = file.readParagraph(upperParagraph).toString();
        String first = joined.substring(0, joinIndex) + removedFromFirst;
        String second = removedFromSecond + joined.substring(joinIndex + 1);

        TextPosition start = new TextPosition(upperParagraph, 0);
        file.deleteRange(start, new TextPosition(upperParagraph + 1, 0));
        file.insert(first + second, start);
    }
}
