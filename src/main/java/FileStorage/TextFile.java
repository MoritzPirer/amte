package FileStorage;

import Controller.View.Line;
import Controller.View.RenderStyle;
import Controller.View.Style;
import Controller.View.TextSpan;
import Shared.TextPosition;

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

    public int length() {
        return buffer.length();
    }
}
