package Controller;

import Controller.View.Line;
import Controller.View.TextSpan;
import Controller.View.ViewComponent;
import FileStorage.TextFile;
import Shared.Size;
import Shared.TextPosition;
import org.w3c.dom.Text;

import java.util.Arrays;

public class ViewPort {
    private final TextFile file;

    private TextPosition cursor;
    private TextPosition top;

    private boolean isRunning = true;

    public ViewPort(TextFile file) {
        this.file = file;

        cursor = new TextPosition(0, 0);
        top = new TextPosition(0, 0);
    }

    public ViewPort(TextFile file, Size size) {
        this.file = file;

        cursor = new TextPosition(0, 0);
        top = new TextPosition(0, 0);
    }

    private static int visualLinesNeeded(String paragraph, Size size) {
        return visualLinesNeeded(paragraph.length(), size.width());
    }

    private static int visualLinesNeeded(Line line, Size size) {
        return visualLinesNeeded(line.length(), size.width());
    }

    private static int visualLinesNeeded(int length, int maxWidth) {
        if (length == 0) {
            return 1;
        }

        int fullLinesNeeded = length / maxWidth;
        boolean hasPartialLine = (length % maxWidth != 0);

        return fullLinesNeeded + (hasPartialLine ? 1 : 0);
    }

    public void insert(String content) {
        file.insert(content, cursor);
    }

    public void moveCursorTo(TextPosition position) {
        cursor = position;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() {
        isRunning = false;
    }

    public ViewComponent getTextArea(Size size) {
        Line[] lines = new Line[size.height()];
        int visualLinesUsed = 0;

        int currentIndex = top.paragraph();
        boolean isFirstParagraph = true;
        while (visualLinesUsed < size.height()) {
            if (file.length() <= currentIndex) {
                Arrays.fill(lines, visualLinesUsed, lines.length, new Line(new TextSpan[0]));
                break;
            }

            Line currentParagraph = file.readParagraph(currentIndex);
            if (isFirstParagraph) {
                currentParagraph = currentParagraph.dropFirstNCharacters(top.character());
                isFirstParagraph = false;
            }

            Line[] visualLines = currentParagraph.splitIntoChunks(size.width());
            int remainingVisualLines = size.height() - visualLinesUsed;
            if (Math.min(visualLines.length, remainingVisualLines) >= 0)
                System.arraycopy(visualLines, 0, lines, visualLinesUsed, Math.min(visualLines.length, remainingVisualLines));

            visualLinesUsed += visualLines.length;
            currentIndex++;
        }

        return new ViewComponent(size, lines);
    }

    public ViewComponent getNumberPanel(Size size) {
        return null;
    }

    public TextPosition getLogicalCursorPosition() {
        return cursor;
    }

    public TextPosition getVisualCursorPosition(Size size) {
        int visualLine = 0;
        boolean isFirstParagraph = true;
        for (int index = top.paragraph(); index < cursor.paragraph(); index++) {
            Line currentParagraph = file.readParagraph(index);
            if (isFirstParagraph) {
                currentParagraph = currentParagraph.dropFirstNCharacters(top.character());
                isFirstParagraph = false;
            }

            int visualLinesOfParagraph = visualLinesNeeded(currentParagraph, size);
            visualLine += visualLinesOfParagraph;
        }

        int lineOfCursorInParagraph = visualLinesNeeded(cursor.character(), size.width());
        if (cursor.character() % size.width() != 0 || cursor.character() == 0) {
            lineOfCursorInParagraph--;
        }
        visualLine += lineOfCursorInParagraph;

        return new TextPosition(visualLine, cursor.character() % size.width());
    }

    public String readRange(TextPosition start, TextPosition end) {
        return file.readRange(start, end);
    }

    public void deleteRange(TextPosition start, TextPosition end) {
        file.deleteRange(start, end);
    }

    public int lengthOfParagraph(int paragraphIndex) {
        return file.lengthOfParagraph(paragraphIndex);
    }
}
