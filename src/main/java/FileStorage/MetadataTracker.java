package FileStorage;

import Shared.Position;
import Shared.Utility.StringUtility;

import java.io.IOException;
import java.util.List;

public class MetadataTracker implements FileBuffer, FileMetadata {
    private final FileBuffer buffer;
    private long wordCount = 0;
    private long characterCount = 0;
    private long paragraphCount = 0;

    MetadataTracker(FileBuffer buffer) {
        assert buffer != null;
        assert !(buffer instanceof MetadataTracker);

        this.buffer = buffer;
    }

    private void changeWordCount(String before, String after) {
        int changeAmount = StringUtility.countWords(before) - StringUtility.countWords(after);
        wordCount += changeAmount;
    }

    private void changeCharacterCount(String before, String after) {
        int changeAmount = before.length() - after.length();
        characterCount += changeAmount;
    }

    private void changeParagraphCount(String before, String after) {
        int changeAmount = StringUtility.countOccurrences(before, '\n') - StringUtility.countOccurrences(after, '\n');
        paragraphCount += changeAmount;
    }

    private void changeCounts(String before, String after) {
        changeParagraphCount(before, after);
        changeWordCount(before, after);
        changeCharacterCount(before, after);
    }

    @Override
    public void insert(String content, Position position) throws InvalidPosition {
        // count in current line
        String before = buffer.readLine(position.row());
        String after = before.substring(0, before.indexOf(position.column()))
            + content
            + before.substring(before.indexOf(position.column()));

        changeCounts(before, after);

        buffer.insert(content, position);
    }

    @Override
    public void delete(Position start, Position end) throws InvalidPosition {
        for (int currentRow = start.row(); currentRow <= end.row(); currentRow++) {
            String before = buffer.readLine(currentRow);
            String after = "";

            if (currentRow == start.row() && currentRow == end.row()) {
                // inline delete
                after = before.substring(0, start.column()) + before.substring(end.column());
            }
            else if (currentRow == start.row()) {
                after = before.substring(0, start.column());
            }
            else if (currentRow == end.row()) {
                after = before.substring(end.column());
            }

            changeCounts(before, after);
        }


        buffer.delete(start, end);
    }

    @Override
    public List<String> readRange(Position start, Position end) throws InvalidPosition {
        return buffer.readRange(start, end);
    }

    @Override
    public String readLine(int row) throws InvalidPosition {
        return buffer.readLine(row);
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

}
