package FileStorage;

import Shared.TextPosition;

import java.util.List;

public class ReadOnlyBufferView {
    private final FileBuffer buffer;

    public ReadOnlyBufferView(FileBuffer buffer) {
        this.buffer = buffer;
    }

    public List<String> readRange(TextPosition start, TextPosition end) throws InvalidPosition {
        return buffer.readRange(start, end);
    }

    public String readParagraph(int paragraphIndex) throws InvalidPosition {
        return buffer.readParagraph(paragraphIndex);
    }

    public int length() {
        return buffer.length();
    }

    public int lengthOfParagraph(int paragraphIndex) throws InvalidPosition {
        return buffer.lengthOfParagraph(paragraphIndex);
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }
}
