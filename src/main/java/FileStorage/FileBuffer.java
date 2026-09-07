package FileStorage;

import Shared.TextPosition;

import java.util.List;

/**
 * Defines the outline of a Container that supports reading and modifying text addressed by paragraphs and
 * the character within that paragraph. Paragraphs are ended by a newline (added implicitly where necessary).
 */
public interface FileBuffer extends ReadableBuffer {
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
     * @param start the start position being deleted. Must be a valid position in the buffer
     * @param end the start position after the deletion. If this is not a position in the buffer, the deletion will get as close to that position as possible.
     * @throws InvalidPosition if start is not a valid position in the buffer or if start is equal to or is after end
     */
    void delete(TextPosition start, TextPosition end) throws InvalidPosition;

    /**
     * delete the entire buffer.
     */
    void clear();

}
