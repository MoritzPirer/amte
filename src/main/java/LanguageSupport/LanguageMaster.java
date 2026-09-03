package LanguageSupport;

import Shared.Utility.FileParsingUtility;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

class LanguageMaster {
    private static LanguageMaster instance = null;

    private final Map<String, Language> languages = new HashMap<>();

    private LanguageMaster() { }

    public static LanguageMaster instance() {
        if (instance == null) {
            instance = new LanguageMaster();
        }

        return instance;
    }

    public boolean isLanguageKnown(String language) {
        if (languages.containsKey(language)) {
            return true;
        }
        LanguageLoader loader = new JsonLanguageLoader();
        if (loader.doesLanguageExist(language)) {
            languages.put(language, loader.load(language));
            return true;
        }

        return false;
    }

    public String get(String key, String language) {
        if (!isLanguageKnown(language)) {
            throw new IllegalArgumentException("Unknown language: " + language);
        }

        return languages.get(language).get(key);
    }
}
