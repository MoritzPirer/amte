package Controller;

import Controller.View.Line;
import Controller.View.TextSpan;
import Controller.View.ViewComponent;
import FileStorage.ReadableBuffer;
import FileStorage.TextFile;
import Shared.Direction;
import Shared.Size;
import Shared.TextPosition;

import java.util.Arrays;

public class ViewPort {
    private final TextFile file;

    private final Cursor cursor;
    private TextPosition top;

    private boolean isRunning = true;

    public ViewPort(TextFile file) {
        this.file = file;

        cursor = new Cursor(new TextPosition(0, 0), file.getBuffer());
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

    public void moveCursorTo(TextPosition position) {
        cursor.moveTo(position);

    }

    public void moveCursor(Direction direction) {
        cursor.move(direction);
    }

    private void updateTopIfNeeded() {

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
        return cursor.getPosition();
    }

    public TextPosition getVisualCursorPosition(Size size) {
        int visualLine = 0;
        boolean isFirstParagraph = true;
        for (int index = top.paragraph(); index < cursor.getPosition().paragraph(); index++) {
            Line currentParagraph = file.readParagraph(index);
            if (isFirstParagraph) {
                currentParagraph = currentParagraph.dropFirstNCharacters(top.character());
                isFirstParagraph = false;
            }

            int visualLinesOfParagraph = visualLinesNeeded(currentParagraph, size);
            visualLine += visualLinesOfParagraph;
        }

        int lineOfCursorInParagraph = visualLinesNeeded(cursor.getPosition().character(), size.width());
        if (cursor.getPosition().character() % size.width() != 0 || cursor.getPosition().character() == 0) {
            lineOfCursorInParagraph--;
        }
        visualLine += lineOfCursorInParagraph;

        return new TextPosition(visualLine, cursor.getPosition().character() % size.width());
    }
}
