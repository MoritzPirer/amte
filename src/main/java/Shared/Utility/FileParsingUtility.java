package Shared.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class FileParsingUtility {

    /**
     * reads the entire content of the file at path into a string. Does not handle files that are
     * too long to fit into a single string. Assumes UTF-8 encoding
     * @param path the path to the file to read
     * @return a string containing the file contents.
     * @throws IOException if the file cannot be read
     */
    public static String readFile(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            return builder.toString();
        }
    }

    /**
     * splits a string containing multiple json objects into individual strings. Assumes a structure
     * where each object is wrapped in curly braces and separated by a single comma, all of which
     * is wrapped in another set of braces
     * @param json a string containing the json objects
     * @return an array of Strings containing a single json object each
     */
    public static String[] splitIntoItems(String json) {
        json = json.substring(1, json.length() - 1); // remove outer brackets

        //split at every "},{", but only consume the comma
        return json.split("(?<=})\\s*,\\s*(?=\\{)");
    }

    /**
     * creates the given path if it does not exist.
     * @param path the path to check. is expected to be a path to a directory, not a file
     */
    public static void ensureDirectoryExists(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            boolean ignored = folder.mkdirs();
        }
    }

    /**
     * @return a string containing a filepath to the data folder
     */
    public static String getBaseFolder() {
        String baseFolder = System.getProperty("user.home") + File.separator + ".strato" + File.separator;
        ensureDirectoryExists(baseFolder);

        return baseFolder;
    }

    /**
     * @return a string containing a filepath to the folder containing the color themes (a subfolder of the data folder)
     */
    public static String getColorThemesFolder() {
        String colorThemesFolder = getBaseFolder() + "themes" + File.separator;
        ensureDirectoryExists(colorThemesFolder);

        return colorThemesFolder;
    }

    /**
     * @return a string containing a filepath to the folder containing the language files (a subfolder of the data folder)
     */
    public static String getLanguageFolder() {
        String languageFolder = getBaseFolder() + "languages" + File.separator;
        ensureDirectoryExists(languageFolder);

        return languageFolder;
    }

    public static String getCodeBlockInfoFolder() {
        String codeBlockInfoFolder = getBaseFolder() + "rendering" + File.separator;
        ensureDirectoryExists(codeBlockInfoFolder);

        return codeBlockInfoFolder;
    }

    /**
     * given a path pointing to a folder, returns an array of paths to the files in that folder (NOT including files in subfolders)
     * @param path the path to check
     * @return an array of paths to folders
     */
    public static Path[] filesInFolder(Path path) {
        File folder = path.toFile();
        if (!folder.isDirectory()) {
            return new Path[0];
        }

        if (!folder.exists()) {
            throw new IllegalArgumentException("Folder " + path + " does not exist");
        }

        File[] files = folder.listFiles();

        assert files != null;

        return Arrays.stream(files)
            .map(File::toPath)
            .toArray(Path[]::new);
    }

}
