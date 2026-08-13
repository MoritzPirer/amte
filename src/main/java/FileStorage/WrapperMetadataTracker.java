package FileStorage;

import Shared.TextPosition;
import Shared.Utility.StringUtility;

import java.util.List;

/**
 * tracks the metadata of a buffer by implementing the FileBuffer interface and wrapping a different buffer.
 * all modification calls are made to the Wrapper, update the internal metadata counts and are forwarded to the inner buffer
 */
public class WrapperMetadataTracker implements FileBuffer, FileMetadataTracker {
    private final FileBuffer buffer;
    private long wordCount = 0;
    private long characterCount = 0;
    private long paragraphCount = 0;

    public WrapperMetadataTracker(FileBuffer buffer) {
        assert buffer != null;
        assert !(buffer instanceof WrapperMetadataTracker);

        //TODO: if buffer not empty do one full parse here
        this.buffer = buffer;
    }

    private void changeWordCount(String before, String after) {
        int changeAmount = StringUtility.countWords(after) - StringUtility.countWords(before);
        wordCount += changeAmount;
    }

    private void changeCharacterCount(String before, String after) {
        int changeAmount = after.length() - before.length();
        characterCount += changeAmount;
    }

    private void changeParagraphCount(String before, String after) {
        int changeAmount = StringUtility.countOccurrences(after, '\n') - StringUtility.countOccurrences(before, '\n');
        paragraphCount += changeAmount;
    }

    private void changeCounts(String before, String after) {
        changeParagraphCount(before, after);
        changeWordCount(before, after);
        changeCharacterCount(before, after);
    }

    @Override
    public void insert(String content, TextPosition position) throws InvalidPosition {
        if (!content.endsWith("\n")) {
            content = content + "\n";
        }

        String before = buffer.readParagraph(position.paragraph());
        String after = content;
        if (!before.isEmpty()) {
            after = before.substring(0, position.character())
            + content
            + before.substring(position.character());
        }

        System.out.println(after + " -> " + StringUtility.countWords(after));
        changeCounts(before, after);

        buffer.insert(content, position);
    }

    @Override
    public void append(String content) {
        if (!content.endsWith("\n")) {
            content = content + "\n";
        }
        changeCounts("", content);
        buffer.append(content);
    }

    @Override
    public void delete(TextPosition start, TextPosition end) throws InvalidPosition {
        if (!end.isAfter(start)) {
            throw new InvalidPosition("start is after end");
        }
        for (int currentRow = start.paragraph(); currentRow <= end.paragraph(); currentRow++) {
            String before = buffer.readParagraph(currentRow);
            String after = "";

            if (currentRow == start.paragraph() && currentRow == end.paragraph()) {
                // inline delete
                after = before.substring(0, start.character()) + before.substring(end.character());
            }
            else if (currentRow == start.paragraph()) {
                after = before.substring(0, start.character());
            }
            else if (currentRow == end.paragraph()) {
                after = before.substring(end.character());
            }

            changeCounts(before, after);
        }


        buffer.delete(start, end);
    }

    @Override
    public List<String> readRange(TextPosition start, TextPosition end) throws InvalidPosition {
        return buffer.readRange(start, end);
    }

    @Override
    public String readParagraph(int paragraphIndex) throws InvalidPosition {
        return buffer.readParagraph(paragraphIndex);
    }

    @Override
    public int length() {
        return buffer.length();
    }

    @Override
    public int lengthOfParagraph(int paragraphIndex) throws InvalidPosition {
        return buffer.lengthOfParagraph(paragraphIndex);
    }

    @Override
    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    @Override
    public long getWordCount() {
        return wordCount;
    }

    @Override
    public long getCharacterCount() {
        return characterCount;
    }

    @Override
    public long getParagraphCount() {
        return paragraphCount;
    }

    @Override
    public FileBuffer getTrackedFileBuffer() {
        return this;
    }
}
