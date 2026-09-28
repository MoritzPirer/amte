package Controller;

import Controller.Services.Clipbobard.ClipboardHandler;
import Controller.Services.Clipbobard.CopyType;
import Controller.Services.History.History;
import Controller.Services.History.StackHistory;
import Controller.TemporaryMessages.Message;
import Controller.TemporaryMessages.TemporaryMessageManager;
import Controller.View.Line;
import Controller.View.Style;
import Controller.View.TextSpan;
import Controller.View.ViewComponent;
import FileStorage.TextFile;
import Settings.SettingsMaster;
import Shared.Direction;
import Shared.Size;
import Shared.TextPosition;
import Shared.Utility.NumberUtility;
import Shared.Utility.StringUtility;

import java.util.Arrays;

public class ViewPort {
    private final TextFile file;

    private final Cursor cursor;
    private TextPosition top;
    private Size textAreaSize;
    private final TemporaryMessageManager messageManager = new TemporaryMessageManager();
    private final History history = new StackHistory();
    private final ClipboardHandler clipboardHandler = new ClipboardHandler();

    private boolean isRunning = true;

    public ViewPort(TextFile file) {
        this.file = file;

        cursor = new Cursor(new TextPosition(0, 0), file.getBuffer());
        top = new TextPosition(0, 0);
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
        updateTopIfNeeded();
    }

    public void moveCursor(Direction direction) {
        cursor.move(direction);
        updateTopIfNeeded();
    }

    private void updateTopIfNeeded() {
        int linesAboveCursor = getVisualCursorPosition(textAreaSize).paragraph() - 1;
        int highestAllowedCursorLine = (int) Math.floor(0.3f * textAreaSize.height());
        int lowestAllowedCursorLine = (int) Math.ceil(0.6f * textAreaSize.height());

        if (linesAboveCursor <= highestAllowedCursorLine) {
            int moveTopUpBy = highestAllowedCursorLine - linesAboveCursor;
            TextPosition position = new TextPosition(top.paragraph() - moveTopUpBy, 0);
            if (position.paragraph() >= 0) {
                top = position;
            }
        }
        else if (linesAboveCursor >= lowestAllowedCursorLine) {
            int moveTopDownBy = linesAboveCursor - lowestAllowedCursorLine;
            TextPosition position = new TextPosition(top.paragraph() + moveTopDownBy, 0);
            if (position.paragraph() < file.length()) {
                top = position;
            }
            //cursor is too far down
            // move top down until in middle third
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() {
        isRunning = false;
        file.stop();
    }

    public void handleFileChange() {
        Message message = file.handleFileChange();
        if (message != null) {
            cursor.setBuffer(file.getBuffer());
            moveCursorTo(new TextPosition(0, 0));
            messageManager.add(message);
        }
    }

    public ViewComponent getTextArea(Size size) {
        this.textAreaSize = size;
        messageManager.updateWidth(textAreaSize.width());

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

            currentParagraph = currentParagraph.replaceTabs();

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

            int visualLines = visualLinesNeeded(currentParagraph.length(), textAreaSize.width());
            String content = generateLineNumber(currentIndex, size.width());
            lines[visualLinesUsed] = new Line(new TextSpan[]{new TextSpan(Style.NUMBERING_STYLE, content)});

            int remainingVisualLines = size.height() - visualLinesUsed;
            for (int i = 1; i < Math.min(visualLines, remainingVisualLines); i++) {
                lines[visualLinesUsed + i] = new Line(new TextSpan[]{});
            }

            visualLinesUsed += visualLines;
            currentIndex++;
        }

        return new ViewComponent(size, lines);
    }

    /**
     * format the number to be displayed for the given paragraph
     * @param paragraphIndex the (0-indexed) paragraph to format the number of
     * @param width the width to pad to
     * @return the formatted number
     */
    private String generateLineNumber(int paragraphIndex, int width) {
        int number;
        boolean isCursorLine;
        if (SettingsMaster.instance().getBoolean("doRelativeNumbers")) {
            if (paragraphIndex == cursor.getPosition().paragraph()) {
                number = paragraphIndex;
                isCursorLine = true;
            }
            else {
                number = Math.abs(paragraphIndex - cursor.getPosition().paragraph());
                isCursorLine = false;
            }
        }
        else {
            number = paragraphIndex + 1;
            isCursorLine = (paragraphIndex == cursor.getPosition().paragraph());
        }

        String numberString = String.valueOf(number);
        if (isCursorLine) {
            return StringUtility.rightAlign(numberString, width);
        }
        return StringUtility.leftAlign(numberString, width);
    }

    public TextPosition getLogicalCursorPosition() {
        return cursor.getPosition();
    }

    public TextPosition getVisualCursorPosition(Size size) {
        if (file.isEmpty()) {
            return new TextPosition(0, 0);
        }

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

        int tabAwareColumn = tabAwareColumn(cursor.getPosition().paragraph(),cursor.getPosition().character());
        int lineOfCursorInParagraph = visualLinesNeeded(
            tabAwareColumn,
            size.width()
        );

        if (cursor.getPosition().character() % size.width() != 0) {
            lineOfCursorInParagraph--;
        }
        if (cursor.getPosition().character() == 0) {
            lineOfCursorInParagraph--;
        }
        visualLine += lineOfCursorInParagraph;

        return new TextPosition(visualLine, tabAwareColumn % size.width());
    }

    private int tabAwareColumn(int paragraphIndex, int logicalColumn) {
        int tabWidth = SettingsMaster.instance().getInt("tabWidth");

        Line paragraph = file.readParagraph(paragraphIndex);

        int visualColumn = 0;
        int logicalColumnsConsumed = 0;

        for (TextSpan span : paragraph.textSpans()) {
            for (char current : span.text().toCharArray()) {
                if (logicalColumnsConsumed >= logicalColumn) {
                    return visualColumn;
                }

                if (current == '\t') {
                    visualColumn = NumberUtility.nextMultipleOf(visualColumn, tabWidth);
                }
                else {
                    visualColumn++;
                }

                logicalColumnsConsumed++;
            }
        }

        return visualColumn;
    }

    public void addTemporaryMessage(Message message) {
        messageManager.add(message);
    }

    public void refreshTemporaryMessages() {
        messageManager.acknowledgeInput();
    }

    public ViewComponent getTemporaryMessages() {
        return messageManager.get();
    }

    public void tickTemporaryMessages() {
        messageManager.tick();
    }

    public History getHistory() {
        return history;
    }

    public String getInternalClipboard() {
        return clipboardHandler.getContent();
    }

    public CopyType getInternalClipboardType() {
        return clipboardHandler.getType();
    }

    public void copy(CopyType type, String content) {
        clipboardHandler.copy(type, content);
    }

}
