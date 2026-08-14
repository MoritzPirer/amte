import FileStorage.FileBuffer;
import FileStorage.InvalidPosition;
import FileStorage.ListFileBuffer;
import FileStorage.WrapperMetadataTracker;
import Shared.TextPosition;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class FileBufferTest {
    private FileBuffer[] getImplementations() {
        return new FileBuffer[]{
            new ListFileBuffer(),
            new WrapperMetadataTracker(new ListFileBuffer())
        };
    }

    @Test
    void testInsertBasic() throws InvalidPosition {
        for (FileBuffer fileBuffer : getImplementations()) {

            // Insert into empty buffer
            String text = "Hello World!";
            fileBuffer.insert(text, new TextPosition(0, 0));
            assertEquals(text.concat("\n"), fileBuffer.readParagraph(0), "Buffer line 0 should match inserted text");

            // Insert to start of line
            fileBuffer.insert("Start: ", new TextPosition(0, 0));
            assertEquals("Start: Hello World!\n", fileBuffer.readParagraph(0));

            // Insert to middle of line
            fileBuffer.insert(" [MID]", new TextPosition(0, 6));
            assertEquals("Start: [MID] Hello World!\n", fileBuffer.readParagraph(0));

            // Insert to end of line / buffer
            int endCol = fileBuffer.readParagraph(0).length() - 1;
            fileBuffer.insert(" :End", new TextPosition(0, endCol));
            assertEquals("Start: [MID] Hello World! :End\n", fileBuffer.readParagraph(0));

            // Multi-line insert
            fileBuffer.insert("Line A\nLine B\nLine C", new TextPosition(0, 0));
            assertEquals("Line A\n", fileBuffer.readParagraph(0));
            assertEquals("Line B\n", fileBuffer.readParagraph(1));
            assertTrue(fileBuffer.readParagraph(2).startsWith("Line CStart:"));

            fileBuffer.append("Line D");
            assertEquals("Line D\n", fileBuffer.readParagraph(fileBuffer.length() - 1));

            // Inserting an empty string (buffer state should not change)
            String beforeEmpty = fileBuffer.readParagraph(0);
            fileBuffer.insert("", new TextPosition(0, 0));
            assertEquals(beforeEmpty, fileBuffer.readParagraph(0));

            // Inserting a null string (should have no effect)
            fileBuffer.insert(null, new TextPosition(0, 0));
            assertEquals(beforeEmpty, fileBuffer.readParagraph(0));
        }
    }

    @Test
    void testInsertFailCases() {
        for (FileBuffer fileBuffer : getImplementations()) {
            // Null position
            assertThrows(Exception.class, () ->
                            fileBuffer.insert("text", null),
                    "Inserting at a null position should throw an exception"
            );

            // Negative paragraph position
            assertThrows(InvalidPosition.class, () ->
                            fileBuffer.insert("text", new TextPosition(-1, 0)),
                    "Negative paragraph position should throw InvalidPosition"
            );

            // Negative character position
            assertThrows(InvalidPosition.class, () ->
                            fileBuffer.insert("text", new TextPosition(0, -1)),
                    "Negative character position should throw InvalidPosition"
            );

            // Valid paragraph, invalid character (beyond line length)
            assertThrows(InvalidPosition.class, () ->
                            fileBuffer.insert("text", new TextPosition(0, 999)),
                    "Column position beyond paragraph length should throw InvalidPosition"
            );

            // Invalid paragraph (beyond buffer paragraph count)
            assertThrows(InvalidPosition.class, () ->
                            fileBuffer.insert("text", new TextPosition(100, 0)),
                    "Row position beyond buffer size should throw InvalidPosition"
            );
        }
    }

    @Test
    void testDeleteBasic() throws InvalidPosition {
        for (FileBuffer fileBuffer : getImplementations()) {
            fileBuffer.insert("Row 1\nRow 2\nRow 3", new TextPosition(0, 0));

            // Delete single character
            fileBuffer.delete(new TextPosition(0, 0), new TextPosition(0, 1));
            assertEquals("ow 1\n", fileBuffer.readParagraph(0));

            // Delete partial paragraph
            fileBuffer.delete(new TextPosition(0, 1), new TextPosition(0, 3));
            assertEquals("o1\n", fileBuffer.readParagraph(0));

            // Delete newline between rows (concatenates Row 1 and Row 2)
            fileBuffer.delete(new TextPosition(0, 1), new TextPosition(1, 0));
            assertEquals("oRow 2\n", fileBuffer.readParagraph(0));

            // Delete full paragraph
            fileBuffer.delete(new TextPosition(0, 0), new TextPosition(1, 0));
            assertEquals("Row 3\n", fileBuffer.readParagraph(0));

            // Delete full buffer content
            int lastRowLen = fileBuffer.readParagraph(0).length();
            fileBuffer.delete(new TextPosition(0, 0), new TextPosition(0, lastRowLen));
            assertEquals("\n", fileBuffer.readParagraph(0));
        }
    }

    @Test
    void testDeleteFailCases() {
        for (FileBuffer fileBuffer : getImplementations()) {
            // Negative start position
            assertThrows(InvalidPosition.class, () ->
                            fileBuffer.delete(new TextPosition(-1, 0), new TextPosition(0, 0)),
                    "Negative start position should throw InvalidPosition"
            );

            // Start position out of range
            assertThrows(InvalidPosition.class, () ->
                            fileBuffer.delete(new TextPosition(100, 0), new TextPosition(101, 0)),
                    "Out-of-range start position should throw InvalidPosition"
            );

            // Start position after end position
            assertThrows(InvalidPosition.class, () ->
                            fileBuffer.delete(new TextPosition(0, 5), new TextPosition(0, 2)),
                    "Start position coming after end position should throw InvalidPosition"
            );
        }
    }

    @Test
    void testReadParagraph() throws InvalidPosition {
        for (FileBuffer fileBuffer : getImplementations()) {
            // Reading non-empty line
            fileBuffer.insert("First Line\n\nThird Line", new TextPosition(0, 0));
            assertEquals("First Line\n", fileBuffer.readParagraph(0));

            // Reading empty line
            assertEquals("\n", fileBuffer.readParagraph(1));

            // Reading from invalid negative line
            assertThrows(InvalidPosition.class, () -> fileBuffer.readParagraph(-1));

            // Reading from invalid line index beyond buffer bounds
            assertThrows(InvalidPosition.class, () -> fileBuffer.readParagraph(10));
        }
    }

    @Test
    void testReadRange() throws InvalidPosition {
        for (FileBuffer fileBuffer : getImplementations()) {
            fileBuffer.insert("Alpha\n\nGamma", new TextPosition(0, 0));

            // Standard multi-line read
            List<String> result = fileBuffer.readRange(new TextPosition(0, 0), new TextPosition(2, 6));
            assertIterableEquals(List.of("Alpha\n", "\n", "Gamma\n"), result);

            // Read starting from an empty line
            List<String> emptyLineRead = fileBuffer.readRange(new TextPosition(1, 0), new TextPosition(2, 0));
            assertIterableEquals(List.of("\n"), emptyLineRead);

            // End position beyond actual buffer end (contract specifies: returns up to available bounds)
            List<String> beyondEndRead = fileBuffer.readRange(new TextPosition(0, 0), new TextPosition(99, 99));
            assertFalse(beyondEndRead.isEmpty(), "Reading beyond buffer end should return existing rows");

            // Negative start position

            assertThrows(InvalidPosition.class, () ->
                    fileBuffer.readRange(new TextPosition(-1, 0), new TextPosition(0, 2))
            );

            // Start position out of range
            assertThrows(InvalidPosition.class, () ->
                    fileBuffer.readRange(new TextPosition(50, 0), new TextPosition(60, 0))
            );

            // End comes before start
            assertThrows(InvalidPosition.class, () ->
                    fileBuffer.readRange(new TextPosition(1, 0), new TextPosition(0, 0))
            );
        }
    }
}