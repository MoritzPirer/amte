package FileStorage.FileIO;

import FileStorage.FileBuffer;
import FileStorage.ListFileBuffer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;

public class DiskFileLoader implements FileLoader {
    @Override
    public void save(Path path, FileBuffer buffer) throws IOException {
        if (path == null) {
            throw new InvalidParameterException("path was null");
        }
        if (buffer == null) {
            throw new InvalidParameterException("buffer was null");
        }


        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (int paragraph = 0; paragraph < buffer.length(); paragraph++) {
                String content = buffer.readParagraph(paragraph);

                if (content != null) {
                    writer.write(content);
                }
            }
        }
    }

    @Override
    public void load(Path path, FileBuffer buffer) throws IOException {
        if (path == null) {
            throw new InvalidParameterException("path was null or empty");
        }
        if (buffer == null) {
            throw new InvalidParameterException("buffer was null");
        }

        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + path);
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.endsWith("\n")) {
                    line = line.concat("\n");
                }

                buffer.append(line);
            }
        }
    }

    @Override
    public FileBuffer load(Path path) throws IOException {
        FileBuffer buffer = new ListFileBuffer();
        load(path, buffer);
        return buffer;
    }
}
