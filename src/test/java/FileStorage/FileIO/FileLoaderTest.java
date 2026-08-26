package FileStorage.FileIO;

import FileStorage.FileBuffer;
import FileStorage.ListFileBuffer;
import Shared.TextPosition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

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
        final String LOAD_FILE = getResourcePath("FileStorage/testLoad.txt");

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
        final String EMPTY_FILE = getResourcePath("FileStorage/testEmpty.txt");

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
                    fileLoader.load(NON_EXISTING_LOAD_FILE, new ListFileBuffer())
            );
        }
    }

    @Test
    void testLoadNullHandling() throws Exception {
        final String LOAD_FILE = getResourcePath("FileStorage/testLoad.txt");

        for (FileLoader fileLoader : getImplementations()) {
            assertThrows(Exception.class, () -> fileLoader.load(LOAD_FILE, null));
            assertThrows(Exception.class, () -> fileLoader.load(null, new ListFileBuffer()));
        }
    }

    @Test
    void testSave() throws Exception {
        final String SAVE_FILE = tempDir.resolve("testSave.txt").toString();

        for (FileLoader fileLoader : getImplementations()) {
            FileBuffer buffer = new ListFileBuffer();
            buffer.append("Line 1\nLine 2\nLine 3");

            fileLoader.save(Path.of(SAVE_FILE), buffer);

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
        final String saveFile = tempDir.resolve("SaveEmptyFile.txt").toString();

        for (FileLoader fileLoader : getImplementations()) {
            fileLoader.save(Path.of(saveFile), new ListFileBuffer());

            FileBuffer verificationBuffer = new ListFileBuffer();
            fileLoader.load(saveFile, verificationBuffer);
            assertTrue(verificationBuffer.isEmpty());
        }
    }

    @Test
    void testSaveNullHandling() throws Exception {
        final String SAVE_FILE = tempDir.resolve("testSaveNull.txt").toString();

        for (FileLoader fileLoader : getImplementations()) {
            assertThrows(Exception.class, () -> fileLoader.save(Path.of(SAVE_FILE), null));
            assertThrows(Exception.class, () -> fileLoader.save(null, new ListFileBuffer()));
        }
    }
}
/*
package FileStorage.FileIO;

import FileStorage.FileBuffer;
import FileStorage.ListFileBuffer;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class FileLoaderTest {

    private FileLoader[] getImplementations() {
        return new FileLoader[]{
            new DiskFileLoader(),
        };
    }

    @Test
    void testLoad() throws Exception {
        final String LOAD_FILE = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("FileStorage/testLoad.txt")).toURI()).toString();
        for (FileLoader fileLoader : getImplementations()) {
            try {
                FileBuffer buffer = new ListFileBuffer();
                fileLoader.load(LOAD_FILE, buffer);

                assertEquals("Line one\n", buffer.readParagraph(0));
                assertEquals("Some more content on the second line\n", buffer.readParagraph(1));
                assertEquals("finally a third line\n", buffer.readParagraph(2));
                assertEquals(3, buffer.length());
            } catch (IOException e) {
                System.out.println(e.getMessage());
                fail();
            }
        }
    }

    @Test
    void testLoadEmpty() throws Exception {
        final String EMPTY_FILE = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("FileStorage/testEmpty.txt")).toURI()).toString();

        for (FileLoader fileLoader : getImplementations()) {
            try {
                FileBuffer buffer = new ListFileBuffer();
                fileLoader.load(EMPTY_FILE, buffer);

                assertTrue(buffer.isEmpty());
            } catch (IOException e) {
                System.out.println(e.getMessage());
                fail();
            }
        }
    }

    @Test
    void testLoadNonExistentFile() throws Exception {
        final String NON_EXISTING_LOAD_FILE = "testLoadNonExistentFile.txt";
        for (FileLoader fileLoader : getImplementations()) {
            assertThrows(IOException.class, () ->
                fileLoader.load(NON_EXISTING_LOAD_FILE, new ListFileBuffer())
            );
        }
    }

    @Test
    void testLoadNullHandling() throws Exception {
        final String LOAD_FILE = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("FileStorage/testLoad.txt")).toURI()).toString();

        for (FileLoader fileLoader : getImplementations()) {
            assertThrows(Exception.class, () ->
                fileLoader.load(LOAD_FILE, null)
            );
            assertThrows(Exception.class, () ->
                    fileLoader.load(null, new ListFileBuffer())
            );
        }
    }

    @Test
    void testSave() throws Exception {
        final String SAVE_FILE = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("FileStorage/testSave.txt")).toURI()).toString();
        for (FileLoader fileLoader : getImplementations()) {

            FileBuffer buffer = new ListFileBuffer();
            buffer.append("Line 1\nLine 2\n Line 3");
            try {
                fileLoader.save(SAVE_FILE, buffer);

                buffer.clear();
                fileLoader.load(SAVE_FILE, buffer);
                assertEquals("Line 1\n", buffer.readParagraph(0));
                assertEquals("Line 2\n", buffer.readParagraph(1));
                assertEquals("Line 3\n", buffer.readParagraph(2));
                assertEquals(3, buffer.length());

            } catch (IOException e) {
                System.out.println(e.getMessage());
                fail();
            }
        }
    }

    @Test
    void testCreateFileIfNeeded() {
        for (FileLoader fileLoader : getImplementations()) {
            String path = "createdFile.txt";

            // ensure file does not exist in case previous run failed before cleanup
            if (new File(path).exists()) {
                boolean ignored = new File(path).delete();
            }

            FileBuffer buffer = new ListFileBuffer();
            buffer.append("hello, world!");

            try {
                fileLoader.save(path, buffer);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                fail();
            }

            assertTrue(new File(path).exists());
            boolean ignored = new File(path).delete();
        }
    }

    @Test
    void testSaveEmpty() {
        for (FileLoader fileLoader : getImplementations()) {
            try {
                String saveFile = "SaveFile.txt";
                fileLoader.save(saveFile, new ListFileBuffer());

                FileBuffer verificationBuffer = new ListFileBuffer();
                fileLoader.load(saveFile, verificationBuffer);
                assertTrue(verificationBuffer.isEmpty());

            } catch (IOException e) {
                System.out.println(e.getMessage());
                fail();
            }
        }
    }

    @Test
    void testSaveNullHandling() throws Exception {
        final String LOAD_FILE = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource("FileStorage/testLoad.txt")).toURI()).toString();
        for (FileLoader fileLoader : getImplementations()) {
            assertThrows(Exception.class, () ->
                    fileLoader.save(LOAD_FILE, null)
            );
            assertThrows(Exception.class, () ->
                    fileLoader.save(null, new ListFileBuffer())
            );
        }
    }
}
 */