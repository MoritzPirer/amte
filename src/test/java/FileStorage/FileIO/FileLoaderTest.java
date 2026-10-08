package FileStorage.FileIO;

import FileStorage.FileBuffer;
import FileStorage.ListFileBuffer;
import Shared.TextPosition;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class FileLoaderTest {

    @TempDir
    Path tempDir; // Automatically created and cleaned up by JUnit 5 for every test

    private FileLoader[] getImplementations() {
        return new FileLoader[]{
                new DiskFileLoader(),
        };
    }

    private String getResourcePath(String resourcePath) throws Exception {
        return Paths.get(
                Objects.requireNonNull(
                        getClass().getClassLoader().getResource(resourcePath),
                        "Resource not found: " + resourcePath
                ).toURI()
        ).toString();
    }

    @Test
    void testLoad() throws Exception {
        final Path LOAD_FILE = Path.of(getResourcePath("FileStorage/testLoad.txt"));

        for (FileLoader fileLoader : getImplementations()) {
            FileBuffer buffer = new ListFileBuffer();
            fileLoader.load(LOAD_FILE, buffer);

            System.out.println("printing buffer:\n" + buffer.readRange(new TextPosition(0,0), new TextPosition(5,0)));
            assertEquals("Line one\n", buffer.readParagraph(0));
            assertEquals("Some more content on the second line\n", buffer.readParagraph(1));
            assertEquals("finally a third line\n", buffer.readParagraph(2));
            assertEquals(3, buffer.length());
        }
    }

    @Test
    void testLoadEmpty() throws Exception {
        final Path EMPTY_FILE = Path.of(getResourcePath("FileStorage/testEmpty.txt"));

        for (FileLoader fileLoader : getImplementations()) {
            FileBuffer buffer = new ListFileBuffer();
            fileLoader.load(EMPTY_FILE, buffer);
            assertTrue(buffer.isEmpty());
        }
    }

    @Test
    void testLoadNonExistentFile() {
        final String NON_EXISTING_LOAD_FILE = tempDir.resolve("nonExistentFile.txt").toString();

        for (FileLoader fileLoader : getImplementations()) {
            assertThrows(IOException.class, () ->
                    fileLoader.load(Path.of(NON_EXISTING_LOAD_FILE), new ListFileBuffer())
            );
        }
    }

    @Test
    void testLoadNullHandling() throws Exception {
        final Path LOAD_FILE = Path.of(getResourcePath("FileStorage/testLoad.txt"));

        for (FileLoader fileLoader : getImplementations()) {
            assertThrows(Exception.class, () -> fileLoader.load(LOAD_FILE, null));
            assertThrows(Exception.class, () -> fileLoader.load(null, new ListFileBuffer()));
        }
    }

    @Test
    void testSave() throws Exception {
        final Path SAVE_FILE = tempDir.resolve("testSave.txt");

        for (FileLoader fileLoader : getImplementations()) {
            FileBuffer buffer = new ListFileBuffer();
            buffer.append("Line 1\nLine 2\nLine 3");

            fileLoader.save(SAVE_FILE, buffer);

            buffer.clear();
            fileLoader.load(SAVE_FILE, buffer);
            assertEquals("Line 1\n", buffer.readParagraph(0));
            assertEquals("Line 2\n", buffer.readParagraph(1));
            assertEquals("Line 3\n", buffer.readParagraph(2));
            assertEquals(3, buffer.length());
        }
    }

    @Test
    void testCreateFileIfNeeded() throws Exception {
        final String path = tempDir.resolve("createdFile.txt").toString();

        for (FileLoader fileLoader : getImplementations()) {
            FileBuffer buffer = new ListFileBuffer();
            buffer.append("hello, world!");

            fileLoader.save(Path.of(path), buffer);
            assertTrue(new File(path).exists());
        }
    }

    @Test
    void testSaveEmpty() throws Exception {
        final Path saveFile = tempDir.resolve("SaveEmptyFile.txt");

        for (FileLoader fileLoader : getImplementations()) {
            fileLoader.save(saveFile, new ListFileBuffer());

            FileBuffer verificationBuffer = new ListFileBuffer();
            fileLoader.load(saveFile, verificationBuffer);
            assertTrue(verificationBuffer.isEmpty());
        }
    }

    @Test
    void testSaveNullHandling() {
        final String SAVE_FILE = tempDir.resolve("testSaveNull.txt").toString();

        for (FileLoader fileLoader : getImplementations()) {
            assertThrows(Exception.class, () -> fileLoader.save(Path.of(SAVE_FILE), null));
            assertThrows(Exception.class, () -> fileLoader.save(null, new ListFileBuffer()));
        }
    }
}