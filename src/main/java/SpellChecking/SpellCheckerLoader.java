package SpellChecking;


import Debugging.DebugLogger;
import Settings.SettingsMaster;
import Shared.Utility.FileParsingUtility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SpellCheckerLoader {
    private static final String EXTENSION = ".txt";

    /**
     * create a SpellChecker containing all words for the given language by reading from the Dictionary Folders
     * @param language the language to load
     * @return a SpellChecker containing all words from the given language files
     */
    public static SpellChecker load(String language) {
        Trie trie = new Trie();

        if (!SettingsMaster.instance().getBoolean("doSpellCheck")) {
            //don't waste memory loading word's that won't be used
            return new SpellChecker(trie, trie);
        }

        DebugLogger.info("SPELLCHECK", "loading spell checker language " + language);
        String languageFile = language + EXTENSION;

        addFromPath(trie, FileParsingUtility.getBaseDictionaryFolder(), languageFile);
        addFromPath(trie, FileParsingUtility.getUserDictionaryFolder(), languageFile);

        System.gc();

        return new SpellChecker(trie, trie);
    }

    /**
     * Construct the file path from folder and language, then add all words to the given addable
     * @param addable the destination to add to
     * @param folder the folder to add from
     * @param language the language to read
     */
    private static void addFromPath(WordAddable addable, String folder, String language) {
        Path path = Path.of(folder + language);

        if (!Files.exists(path)) {
            DebugLogger.warn("SPELLCHECK", "spell addable file " + path + " does not exist");
            return;
        }

        try {
            addAll(addable, path);
        }
        catch (IOException e) {
            DebugLogger.warn("SPELLCHECK",
                "problem during loading of language file, possibly skipping words: " + e.getMessage());
        }
    }

    /**
     * add all words from the file pointed to by filePath to addable
     * @param addable the destination to add to
     * @param filePath the path of the file to read from
     * @throws IOException if the file could not be properly read
     */
    private static void addAll(WordAddable addable, Path filePath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                String[] words = line.toLowerCase().split("\\s+");
                for (String word : words) {
                    addable.add(word);
                    count++;
                }
            }

            DebugLogger.info("SPELLCHECK", "added " + count + " words from " + filePath);
        }

    }

    /**
     * append the given word to the user dictionary for the given language
     * @param word the word to add
     * @param language the language to add the word to
     * @throws IOException if the file could not be written to
     */
    public static void store(String word, String language) throws IOException {
        String folder = FileParsingUtility.getUserDictionaryFolder();
        Path path = Path.of(folder + language + EXTENSION);

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(word);
        }
    }
}
