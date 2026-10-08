package FileStorage;

import Shared.TextPosition;

import java.util.List;

public interface ReadableBuffer {
    /**
     * reads the content of the buffer beginning at start and going up to the character before end.
     * @param start the start position being read. Must be a valid position in the buffer
     * @param end the start position after teh read. Must be after start. If this is not a position in the buffer,
     *            the read will get as close to that position as possible
     * @return a String of the content that was read
     * @throws InvalidPosition if start is not a valid position in the buffer or if start is equal to or after end
     */
    String readRange(TextPosition start, TextPosition end) throws InvalidPosition;

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
     * checks if the buffer is empty.
     * @return true if the buffer is empty, false otherwise
     */
    boolean isEmpty();

    /**
     * creates a new ReadableBuffer that is a deep copy of the current state in a thread-safe way
     * @return the current state of the buffer
     */
    ReadableBuffer snapshot();
}
