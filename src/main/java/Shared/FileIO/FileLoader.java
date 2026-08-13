package Shared.FileIO;

import FileStorage.FileBuffer;
import FileStorage.InvalidPosition;

import java.io.*;

//TODO make this an interface
public class FileLoader {
    public static void save(FileBuffer buffer, String path) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));

        for (int paragraph = 0; paragraph < buffer.length(); paragraph++) {
            writer.write(buffer.readParagraph(paragraph));
        }

        writer.close();
    }

    public static void load(String path, FileBuffer buffer) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));

        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.endsWith("\n")) {
                line = line + "\n";
            }

            buffer.append(line);
        }

        reader.close();
    }
}
