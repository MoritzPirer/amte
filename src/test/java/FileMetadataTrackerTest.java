import FileStorage.FileBuffer;
import FileStorage.FileMetadataTracker;
import FileStorage.ListFileBuffer;
import FileStorage.WrapperMetadataTracker;
import Shared.TextPosition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FileMetadataTrackerTest {
    private FileMetadataTracker[] getImplementations() {
        return new FileMetadataTracker[]{
                new WrapperMetadataTracker(new ListFileBuffer())
        };
    }

    @Test
    void testCountAfterInsert() {
        for (FileMetadataTracker tracker : getImplementations()) {
            FileBuffer fileBuffer = tracker.getTrackedFileBuffer();

            String text = "Hello World!";
            fileBuffer.insert(text, new TextPosition(0, 0));
            assertEquals(12L, tracker.getCharacterCount(), "character count should be 12");
            assertEquals(2L, tracker.getWordCount(), "word count should be 2");
            assertEquals(1L, tracker.getParagraphCount(), "paragraph count should be 1");

            fileBuffer.insert("Start: ", new TextPosition(0, 0));
            assertEquals(19L, tracker.getCharacterCount(), "character count should be 19");
            assertEquals(3L, tracker.getWordCount(), "word count should be 3");
            assertEquals(1L, tracker.getParagraphCount(), "paragraph count should be 1");

            fileBuffer.insert(" [MID]", new TextPosition(0, 6));
            assertEquals(25, tracker.getCharacterCount(), "character count should be 25");
            assertEquals(4L, tracker.getWordCount(), "word count should be 4");
            assertEquals(1L, tracker.getParagraphCount(), "paragraph count should be 1");

            int endCol = fileBuffer.lengthOfParagraph(0) - 1;
            fileBuffer.insert(" :End", new TextPosition(0, endCol));
            assertEquals(30L, tracker.getCharacterCount(), "character count should be 30");
            assertEquals(5L, tracker.getWordCount(), "word count should be 5");
            assertEquals(1L, tracker.getParagraphCount(), "paragraph count should be 1");

            fileBuffer.insert("Line A\nLine B\nLine C", new TextPosition(0, 0));
            assertEquals(48, tracker.getCharacterCount(), "character count should be 48");
            assertEquals(10L, tracker.getWordCount(), "word count should be 10");
            assertEquals(3L, tracker.getParagraphCount(), "paragraph count should be 3");
        }
    }

    @Test
    void testCountAfterDelete() {
        for (FileMetadataTracker tracker : getImplementations()) {
            FileBuffer fileBuffer = tracker.getTrackedFileBuffer();

            fileBuffer.insert("Row 1\nRow 2\nRow 3", new TextPosition(0, 0));

            fileBuffer.delete(new TextPosition(0, 0), new TextPosition(0, 1));
            assertEquals(14, tracker.getCharacterCount(), "character count should be 14");
            assertEquals(6L, tracker.getWordCount(), "word count should be 6");
            assertEquals(3L, tracker.getParagraphCount(), "paragraph count should be 3");

            fileBuffer.delete(new TextPosition(0, 1), new TextPosition(0, 3));
            assertEquals(12, tracker.getCharacterCount(), "character count should be 12");
            assertEquals(5L, tracker.getWordCount(), "word count should be 5");
            assertEquals(3L, tracker.getParagraphCount(), "paragraph count should be 3");

            fileBuffer.delete(new TextPosition(0, 1), new TextPosition(1, 0));

            assertEquals(11, tracker.getCharacterCount(), "character count should be 11");
            assertEquals(4L, tracker.getWordCount(), "word count should be 4");
            assertEquals(2L, tracker.getParagraphCount(), "paragraph count should be 2");
            assertEquals("oRow 2\n", fileBuffer.readParagraph(0));
        }
    }
}
