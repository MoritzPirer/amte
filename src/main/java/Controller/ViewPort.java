package Controller;

import FileStorage.TextFile;
import Shared.Size;
import Shared.TextPosition;

public class ViewPort {
    private TextFile file;

    private TextPosition cursor;
    private TextPosition top;
    private TextPosition bottom;


    public ViewPort(TextFile file, Size size) {
        this.file = file;

        cursor = new TextPosition(0, 0);
        top = new TextPosition(0, 0);
        bottom = findBottom(size);
    }

    private TextPosition findBottom(Size size) {
        int lines = 0;
        int currentParagraph = 0;
        while (lines < file.length()) {
            String paragraph = file.readParagraph(currentParagraph);
            int linesOfParagraph = visualLinesNeeded(paragraph, size);

            if (lines + linesOfParagraph < size.height()) {
                lines += linesOfParagraph;
                currentParagraph++;
                continue;
            }
            else if (lines + linesOfParagraph == size.height()) {
                return new TextPosition(currentParagraph + 1, 0);
            }
            else {
                // only need some of the visual lines
                int linesNeeded = size.height() - lines;
                int character = size.width() * linesNeeded;

                return new TextPosition(currentParagraph, character);
            }
        }

        return new TextPosition(
            file.length(),
            0
        );
    }

    private static int visualLinesNeeded(String paragraph, Size size) {
        if (paragraph.isEmpty()) {
            return 1; // empty paragraph still needs a line
        }

        int fullLinesNeeded = paragraph.length() / size.width();
        boolean hasPartialLine = (paragraph.length() % size.width() != 0);

        return fullLinesNeeded + (hasPartialLine ? 1 : 0);
    }

}
