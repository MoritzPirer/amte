package FileStorage;

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

    public String readParagraph(int paragraphIndex) {
        return buffer.readParagraph(paragraphIndex);
    }

    public int length() {
        return buffer.length();
    }
}
