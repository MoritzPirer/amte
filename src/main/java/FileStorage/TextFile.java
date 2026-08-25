package FileStorage;

import Controller.View.Line;
import Controller.View.RenderStyle;
import Controller.View.Style;
import Controller.View.TextSpan;
import Shared.TextPosition;

import java.util.List;

public class TextFile {
    private final String filePath;
    private final String fileName;

    private final FileBuffer buffer;
    private final FileMetadataTracker tracker;

    private SaveState saveState;

    public TextFile(String filePath, String fileName, FileBuffer buffer, FileMetadataTracker tracker) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.buffer = buffer;
        this.tracker = tracker;
        saveState = SaveState.MODIFIED; //TEMP
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
        //TODO WHEN [Rendering Component is being implemented] DO [rewrite this function to retrieve Style from Rendering Component]
        Style style = new Style(false, false, RenderStyle.TEXT);
        TextSpan span = new TextSpan(style, buffer.readParagraph(paragraphIndex));
        return new Line(new TextSpan[]{span});
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
    }

    public int length() {
        return buffer.length();
    }

    public int lengthOfParagraph(int paragraphIndex) {
        return buffer.lengthOfParagraph(paragraphIndex);
    }
}
