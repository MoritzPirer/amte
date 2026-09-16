package FileStorage;

import Controller.View.Line;
import Controller.View.RenderStyle;
import Controller.View.Style;
import Controller.View.TextSpan;
import FileStorage.FileIO.DiskFileLoader;
import FileStorage.FileIO.FileLoader;
import Rendering.DoubleBufferRenderer;
import Rendering.Renderer;
import Shared.TextPosition;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class TextFile {
    private Path filePath;
    private String fileName;

    private final FileBuffer buffer;
    private final FileMetadataTracker tracker;

    private SaveState saveState;

    private final Renderer renderer;

    public TextFile(Path filePath, String fileName, FileBuffer buffer, FileMetadataTracker tracker, SaveState saveState) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.buffer = buffer;
        this.tracker = tracker;
        this.saveState = saveState;

        renderer = new DoubleBufferRenderer(buffer);
        renderer.start();
    }

    public void setFileName(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            this.fileName = fileName;
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void save() {
        filePath = Path.of(filePath.getParent() + File.separator + fileName);

        FileLoader loader = new DiskFileLoader();
        try {
            loader.save(filePath, buffer);
            saveState = SaveState.SAVED;
        } catch (IOException ignored) {
            // user will be informed that saving failed based on saveState
        }
    }

    public boolean isSaved() {
        return saveState == SaveState.SAVED;
    }

    public SaveState getSaveState() {
        return saveState;
    }

    public FileBuffer getBuffer() {
        return buffer;
    }

    public void insert(String content, TextPosition position) {
        buffer.insert(content, position);
        markAsModified();
    }

    private void markAsModified() {
        if (saveState != SaveState.CREATED) {
            saveState = SaveState.MODIFIED;
        }
    }

    public Line readParagraph(int paragraphIndex) {
        return Line.of(buffer.readParagraph(paragraphIndex), renderer.getStyle(paragraphIndex));
    }

    public String readRange(TextPosition start, TextPosition end) {
        //TODO WHEN [anytime] DO [rewrite FileBuffer interface (and corresponding tests) to return String, not List<String>]
        List<String> list = buffer.readRange(start, end);
        StringBuilder builder = new StringBuilder();
        for (String string : list) {
            builder.append(string);
        }

        return builder.toString();
    }

    public void deleteRange(TextPosition start, TextPosition end) {
        buffer.delete(start, end);
        markAsModified();
    }

    public int length() {
        return buffer.length();
    }

    public int lengthOfParagraph(int paragraphIndex) {
        return buffer.lengthOfParagraph(paragraphIndex);
    }

    public int getWordCount() {
        //TODO WHEN [anytime] DO [change tracker interface to return int instead of long]
        return (int) tracker.getWordCount();
    }

    public int getCharacterCount() {
        return (int) tracker.getCharacterCount();
    }

    public int getParagraphCount() {
        return (int) tracker.getParagraphCount();
    }

    public TextPosition nextPosition(TextPosition position) {
        if (buffer.isEmpty()) {
            return new TextPosition(position.paragraph(), position.character() + 1);
        }
        if (position.paragraph() < buffer.length() && position.character() >= buffer.lengthOfParagraph(position.paragraph()) - 1) {
            return new TextPosition(position.paragraph() + 1, 0);
        }

        return new TextPosition(position.paragraph(), position.character() + 1);
    }

    public TextPosition previousPosition(TextPosition position) {
        if (position.equals(0, 0)) {
            return null;
        }
        if (position.character() == 0) {
            return new TextPosition(position.paragraph() - 1, buffer.lengthOfParagraph(position.paragraph() - 1) - 1);
        }

        return new TextPosition(position.paragraph(), position.character() - 1);
    }

    public boolean positionExists(TextPosition position) {
        if (position.paragraph() < 0 || position.paragraph() >= buffer.length()) {
            return false;
        }

        if (position.character() < 0 || position.character() >= buffer.lengthOfParagraph(position.paragraph())) {
            return false;
        }

        return true;
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }
}
