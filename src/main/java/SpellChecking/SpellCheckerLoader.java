package SpellChecking;


import Debugging.DebugLogger;
import Shared.Utility.FileParsingUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SpellCheckerLoader {
    public static void populate(WordChecker wordChecker, String language) {
        DebugLogger.info("SPELLCHECK", "loading spell checker language " + language);
        String languageFile = language + ".txt";
        addFromPath(wordChecker, FileParsingUtility.getBaseDictionaryFolder(), languageFile);
        addFromPath(wordChecker, FileParsingUtility.getUserDictionaryFolder(), languageFile);

        System.gc();
    }

    private static void addFromPath(WordChecker checker, String folder, String language) {
        Path path = Path.of(folder + language);

        if (!Files.exists(path)) {
            DebugLogger.warn("SPELLCHECK", "spell checker file " + path + " does not exist");
            return;
        }

        try {
            addAll(checker, path);
        }
        catch (IOException e) {
            DebugLogger.warn("SPELLCHECK",
                "problem during loading of language file, possibly skipping words: " + e.getMessage());
        }
    }


    private static void addAll(WordChecker wordChecker, Path dictionaryPath) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(dictionaryPath, StandardCharsets.UTF_8)) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] words = line.toLowerCase().split("\\s+");
                for (String word : words) {
                    wordChecker.add(word);
                    count++;
                }
            }
            DebugLogger.info("SPELLCHECK", "added " + count + " words from " + dictionaryPath);
        }

    }
}
