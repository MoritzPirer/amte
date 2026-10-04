package Controller.Action;

import Controller.ViewPortManager.ViewPortManager;
import FileStorage.TextFile;
import Shared.TextPosition;
import Shared.Utility.StringUtility;

/**
 * join two neighbouring paragraphs into one
 */
public class ParagraphJoiningAction extends Action {
    private final int upperParagraph;

    private String removedFromFirst;
    private String removedFromSecond;
    private int joinIndex;

    public ParagraphJoiningAction(int upperParagraph) {
        this.upperParagraph = upperParagraph;
    }


    @Override
    public void apply(ViewPortManager viewPortManager) {
        TextFile file = viewPortManager.current().getFile();

        if (upperParagraph >= file.length() - 1) {
            return;
        }

        String first = file.readParagraph(upperParagraph).toString();
        String second = file.readParagraph(upperParagraph + 1).toString();

        int firstTrailingWhiteSpace = StringUtility.countTrailingWhitespace(first);
        int secondLeadingWhiteSpace = StringUtility.countLeadingWhitespace(second);

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
    public void undo(ViewPortManager viewPortManager) {
        TextFile file = viewPortManager.current().getFile();

        String joined = file.readRawParagraph(upperParagraph);
        String first = joined.substring(0, joinIndex) + removedFromFirst;
        String second = removedFromSecond + joined.substring(joinIndex + 1);

        TextPosition start = new TextPosition(upperParagraph, 0);
        file.deleteRange(start, new TextPosition(upperParagraph + 1, 0));
        file.insert(first + second, start);
    }
}
