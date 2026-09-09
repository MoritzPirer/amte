package FileStorage;

import Shared.TextPosition;
import Shared.Utility.StringUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements the FileBuffer interface as a List of Strings, with each list element being one paragraph / paragraph of content
 */
public class ListFileBuffer implements FileBuffer {
    private final List<String> paragraphs;

    public ListFileBuffer() {
        paragraphs = new ArrayList<>();
    }

    private void checkPosition(TextPosition position) throws InvalidPosition {
        if (position.paragraph() < 0 || position.paragraph() > paragraphs.size()) {
            throw new InvalidPosition("paragraph " + position.paragraph() + " is out of range");
        }

        if (position.paragraph() == paragraphs.size()) {
            if (position.character() != 0) {
                throw new InvalidPosition("character must be 0 when paragraph indicates adding a new line");
            }
            return;
        }

        if (position.character() < 0 || position.character() >= paragraphs.get(position.paragraph()).length()) {
            throw new InvalidPosition("character " + position.character() + " is out of range");
        }
    }

    @Override
    public void insert(String content, TextPosition position) throws InvalidPosition {
        checkPosition(position);

        if (content == null || content.isEmpty()) {
            return;
        }

        if (position.paragraph() == paragraphs.size()) {
            append(content);
            return;
        }

        String currentLine = paragraphs.get(position.paragraph());
        String beforeInsert = currentLine.substring(0, position.character());
        String afterInsert = currentLine.substring(position.character());

        if (content.equals("\n")) {
            paragraphs.set(position.paragraph(), beforeInsert + "\n");
            paragraphs.add(position.paragraph() + 1, afterInsert);
            return;
        }
        else if (!content.contains("\n")) {
            // inline insert
            String result = beforeInsert + content + afterInsert;
            paragraphs.set(position.paragraph(), result);
            return;
        }

        List<String> lines = StringUtility.splitAfterDelimiter(content, '\n');
        assert !lines.isEmpty();
        lines.set(0, beforeInsert + lines.getFirst());
        if (lines.getLast().endsWith("\n")) {
            lines.add(afterInsert);
        }
        else {
            lines.set(lines.size() - 1, lines.getLast() + afterInsert);
        }

        paragraphs.remove(position.paragraph());
        paragraphs.addAll(position.paragraph(), lines);

    }

    @Override
    public void append(String content) {
        List<String> lines = StringUtility.splitAfterDelimiter(content, '\n');
        assert !lines.isEmpty();

        if (!lines.getLast().endsWith("\n")) {
            lines.set(lines.size() - 1, lines.getLast().concat("\n"));
        }

        paragraphs.addAll(lines);
    }

    @Override
    public void delete(TextPosition start, TextPosition end) throws InvalidPosition {
        checkPosition(start);
        if (!end.isAfter(start)) {
            throw new InvalidPosition("start is after end");
        }
        
        String beforeDelete = paragraphs.get(start.paragraph()).substring(0, start.character());

        TextPosition deletionEnd = getDeletionEnd(end);

        int rowsToDelete = deletionEnd.paragraph() - start.paragraph() + 1;
        String afterDelete = paragraphs.get(deletionEnd.paragraph()).substring(deletionEnd.character());

        for (int i = 0; i < rowsToDelete; i++) {
            paragraphs.remove(start.paragraph());
        }

        if (!beforeDelete.isEmpty() || !afterDelete.isEmpty()) {
            paragraphs.add(start.paragraph(), beforeDelete + afterDelete);
        }
    }

    @Override
    public void clear() {
        paragraphs.clear();
    }

    /**
     * finds a position in the buffer that is as close as possible to the given end. If the paragraph does not exist,
     * the end position in the buffer is used. if the paragraph is valid but the column is not, the end character in that paragraph is used
     * @param end the possibly invalid end to correct
     * @return the corrected end
     */
    private TextPosition getDeletionEnd(TextPosition end) {
        TextPosition deletionEnd;
        if (end.paragraph() >= paragraphs.size()) {
            deletionEnd = new TextPosition(
                    paragraphs.size() - 1,
                    paragraphs.getLast().length() - 1
            );
        }
        else if (end.character() >= paragraphs.get(end.paragraph()).length()) {
            deletionEnd = new TextPosition(end.paragraph(), paragraphs.get(end.paragraph()).length());
        }
        else {
            deletionEnd = end;
        }

        return deletionEnd;
    }

    @Override
    public List<String> readRange(TextPosition start, TextPosition end) throws InvalidPosition {
        checkPosition(start);
        if (!end.isAfter(start)) {
            throw new InvalidPosition("start (" + start + ") is after end (" + end + ")");
        }

        List<String> result = new ArrayList<>();

        for (int paragraph = start.paragraph(); paragraph <= end.paragraph(); paragraph++) {
            if (paragraph >= paragraphs.size()) {
                break;
            }

            int startIndex = paragraph == start.paragraph()? start.character(): 0;
            int endIndex = paragraph == end.paragraph()? end.character(): paragraphs.get(paragraph).length();

            if (paragraph == end.paragraph() && endIndex == 0) {
                continue;
            }

            result.add(paragraphs.get(paragraph).substring(startIndex, endIndex));
        }

        return result;
    }

    @Override
    public String readParagraph(int paragraphIndex) throws InvalidPosition {
        if (paragraphIndex < 0 || paragraphIndex >= paragraphs.size()) {
            for (String line : paragraphs) {
                System.out.println(line.replace("\n", "<\\n>"));
            }
            throw new InvalidPosition("paragraph " + paragraphIndex + " is out of range");
        }
        
        return paragraphs.get(paragraphIndex);
    }

    @Override
    public int length() {
        return paragraphs.size();
    }

    @Override
    public int lengthOfParagraph(int paragraphIndex) throws InvalidPosition {
        if (paragraphIndex < 0 || paragraphIndex >= paragraphs.size()) {
            throw new InvalidPosition("paragraph " + paragraphIndex + " is out of range");
        }

        return paragraphs.get(paragraphIndex).length();
    }

    @Override
    public boolean isEmpty() {
        return paragraphs.isEmpty();
    }
}
