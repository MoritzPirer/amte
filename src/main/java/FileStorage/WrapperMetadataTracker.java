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

        this.buffer = buffer;

        if (buffer.isEmpty()) {
            return;
        }

        for (int i = 0; i < buffer.length(); i++) {
            changeCounts("", buffer.readParagraph(i));
        }
    }

    private void changeWordCount(String before, String after) {
        int changeAmount = StringUtility.countWords(after) - StringUtility.countWords(before);
        wordCount += changeAmount;
    }

    private void changeCharacterCount(String before, String after) {
        int changeAmount = StringUtility.countCharactersBlacklisted(after, "\n") - StringUtility.countCharactersBlacklisted(before, "\n");
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

    private void checkPosition(TextPosition position) throws InvalidPosition {
        if (position.paragraph() < 0 || position.paragraph() > buffer.length()) {
            throw new InvalidPosition("paragraph " + position.paragraph() + " is out of range");
        }
        if (position.paragraph() == buffer.length()) {
            if (position.character() != 0) {
                throw new InvalidPosition("character must be 0 when paragraph indicates adding a new line");
            }
            return;
        }
        if (position.character() < 0 || position.character() >= buffer.lengthOfParagraph(position.paragraph())) {
            throw new InvalidPosition("character " + position.character() + " is out of range");
        }
    }

    @Override
    public void insert(String content, TextPosition position) throws InvalidPosition {
        checkPosition(position);

        if (buffer.isEmpty()) {
            append(content);
            return;
        }
        String before = buffer.length() == position.paragraph()? "" : buffer.readParagraph(position.paragraph());
        String after = content;

        if (!before.isEmpty()) {
            after = before.substring(0, position.character())
            + content
            + before.substring(position.character());
        }

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
        checkPosition(start);

        if (!end.isAfter(start)) {
            throw new InvalidPosition("start is after end");
        }

        if (start.paragraph() == end.paragraph()) {
            inlineDelete(start, end);
        }
        else {
            multiLineDelete(start, end);
        }

        buffer.delete(start, end);
    }

    /**
     * delete all characters between start (inclusive) and end (exclusive), assuming start and row ar in the same paragraph
     * @param start the first character to delete
     * @param end the first character after the deletion
     */
    private void inlineDelete(TextPosition start, TextPosition end) {
        assert start.paragraph() == end.paragraph();

        String lineBeforeDelete = buffer.readParagraph(start.paragraph());
        String beforeDeletedContent = lineBeforeDelete.substring(0, start.character());
        String afterDeletedContent = lineBeforeDelete.substring(end.character());

        String lineAfterDelete = beforeDeletedContent + afterDeletedContent;

        changeWordCount(lineBeforeDelete, lineAfterDelete);
        changeCharacterCount(lineBeforeDelete, lineAfterDelete);
    }

    /**
     * delete all characters between start (inclusive) and end (exclusive), assuming end is on a different paragraph
     * @param start the first character to delete
     * @param end the first character after the deletion
     */
    private void multiLineDelete(TextPosition start, TextPosition end) {
        assert start.paragraph() < end.paragraph();

        String beforeDeletedContent = buffer.readParagraph(start.paragraph()).substring(0, start.character());
        String afterDeletedContent = buffer.readParagraph(end.paragraph()).substring(end.character());
        String lineAfterDelete = beforeDeletedContent + afterDeletedContent;

        for (int i = start.paragraph(); i <= end.paragraph(); i++) {
            changeWordCount(buffer.readParagraph(i), "");
            changeCharacterCount(buffer.readParagraph(i), "");
            paragraphCount--;
        }

        changeWordCount("", lineAfterDelete);
        changeCharacterCount("", lineAfterDelete);
        paragraphCount++;
    }

    @Override
    public void clear() {
        paragraphCount = 0;
        wordCount = 0;
        characterCount = 0;

        buffer.clear();
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
