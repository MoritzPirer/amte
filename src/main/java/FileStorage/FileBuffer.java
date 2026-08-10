package FileStorage;

import Shared.Position;

import java.util.List;

public interface FileBuffer {
    /**
     * inserts the given content into the buffer at the specified position
     * @param content the content to add. May be multiple paragraphs separated by newlines, but these will be treated as
     *                distinct rows in future method calls
     * @param position the position to start inserting at. Row must be in range (0 - number of rows), column must be in range (0 - length of row)
     * @throws InvalidPosition if the given position is out of range
     */
    void insert(String content, Position position) throws InvalidPosition;

    /**
     * Deletes the content between the two positions, including the start but excluding the end.
     * Deleting the newline at the end of a row concatenates rows
     * @param start the position of the first character to be deleted
     * @param end the position after the last character to be deleted. Can be a non-existent position but must be after start
     * @throws InvalidPosition if start is not a valid position or end comes before start
     */
    void delete(Position start, Position end) throws InvalidPosition;

    /**
     * reads the content between the two positions, including the start but excluding the end.
     * @param start the first position to read
     * @param end the position after the last character to read. Can be a non-existent position but must be after start
     * @return a list where each entry is a string containing one row of the buffer
     * @throws InvalidPosition if start is not a valid position or end comes before start
     */
    List<String> readRange(Position start, Position end) throws InvalidPosition;

    /**
     * reads the entirety of a line from the buffer
     * @param row the row to read
     * @return the row at the given index
     * @throws InvalidPosition if row is not a valid index in the buffer
     */
    String readLine(int row) throws InvalidPosition;
}
