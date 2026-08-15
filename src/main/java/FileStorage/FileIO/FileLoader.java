package FileStorage.FileIO;

import FileStorage.FileBuffer;

import java.io.IOException;

public interface FileLoader {
    /**
     * Saves the contents of buffer to the file located at path
     * @param path the file path to write to. If the file does not exist, it will be created
     * @param buffer the buffer to write
     * @throws IOException if writing failed
     */
    void save(String path, FileBuffer buffer) throws IOException;

    /**
     * Reads the contents of the file located at path into buffer. Existing contents of buffer will be cleared.
     * @param path the file path to read from.
     * @param buffer the buffer to read to. Existing contents are overwritten
     * @throws IOException if reading failed (e.g. because the file does not exist)
     */
    void load(String path, FileBuffer buffer) throws IOException;
}
