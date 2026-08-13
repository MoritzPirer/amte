package FileStorage;

import Shared.TextPosition;

import java.util.List;

/**
 * Defines the outline of a Container that supports reading and modifying text addressed by rows (=paragraphs) and the character within that paragraph
 */
public interface FileBuffer {
    void insert(String content, TextPosition position) throws InvalidPosition;
    void append(String content);

    void delete(TextPosition start, TextPosition end) throws InvalidPosition;
    //void clear();

    List<String> readRange(TextPosition start, TextPosition end) throws InvalidPosition;
    String readParagraph(int paragraphIndex) throws InvalidPosition;

    int length();
    int lengthOfParagraph(int paragraphIndex) throws InvalidPosition;
    boolean isEmpty();
}
