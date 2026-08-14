package FileStorage;

import Shared.TextPosition;

import java.util.List;

/**
 * Defines the outline of a Container that supports reading and modifying text addressed by paragraphs and
 * the character within that paragraph. Paragraphs are ended by a newline (added implicitly where necessary).
 * The buffer will always have at least one paragraph (i.e. an empty buffer still contains one empty paragraph "\n")
 */
public interface FileBuffer {
    /**
     * inserts the content at the given position.
     * @param content the content to add. May contain newlines to create new lines.
     * @param position the position to insert in. If the paragraph is in the range [0, n-1] (with n being the number of paragraphs),
     *                 then the column specifies where in that paragraph the insertion starts. If the paragraph is n, the column has
     *                 to be 0, which will act as if append() had been called.
     * @throws InvalidPosition if the paragraph is not in the range [0, n] or if the column is not in the range [0, m-1]
     */
    void insert(String content, TextPosition position) throws InvalidPosition;

    /**
     * appends the content to the end of the buffer, starting in a new paragraph
     * @param content the content to add. May contain newlines to create multiple lines
     */
    void append(String content);

    /**
     * deletes the content beginning at start and going up to the character before end
     * @param start the first position being deleted. Must be a valid position in the buffer
     * @param end the first position after the deletion. If this is not a position in the buffer, the deletion will get as close to that position as possible.
     * @throws InvalidPosition if start is not a valid position in the buffer or if start is equal to or is after end
     */
    void delete(TextPosition start, TextPosition end) throws InvalidPosition;

    /**
     * delete the entire buffer. After calling this, the buffer will contain one paragraph containing only a newline character
     */
    void clear();

    /**
     * reads the content of the buffer beginning at start and going up to the character before end. Each paragraph is
     * returned as one entry in the list
     * @param start the first position being read. Must be a valid position in the buffer
     * @param end the first position after teh read. Must be after start. If this is not a position in the buffer,
     *            the read will get as close to that position as possible
     * @return a list where each element is one paragraph that was read
     * @throws InvalidPosition if start is not a valid position in th ebuffer or if start is equal to or after end
     */
    List<String> readRange(TextPosition start, TextPosition end) throws InvalidPosition;

    /**
     * reads a single paragraph from the buffer
     * @param paragraphIndex the index of the paragraph to read
     * @return the content of the requested paragraph
     * @throws InvalidPosition if paragraphIndex is negative or greater than or equal to the value returned by FileBuffer.length()
     */
    String readParagraph(int paragraphIndex) throws InvalidPosition;

    /**
     * @return the number of paragraphs in the buffer
     */
    int length();

    /**
     * @param paragraphIndex the paragraph to find the length of
     * @return the length of the requested paragraph
     * @throws InvalidPosition if paragraphIndex is negative or greater than or equal to the value returned by FileBuffer.length()
     */
    int lengthOfParagraph(int paragraphIndex) throws InvalidPosition;

    /**
     * checks if the buffer is empty. Note that the buffer will always contain at least one paragraph containing at least the newline character.
     * I.e. a buffer containing only one newline is considered empty
     * @return true if the buffer is empty, false otherwise
     */
    boolean isEmpty();
}
