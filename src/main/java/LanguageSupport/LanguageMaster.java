package LanguageSupport;

import Shared.Utility.FileParsingUtility;

import java.nio.file.Path;
import java.util.Map;

class LanguageMaster {
    private static LanguageMaster instance = null;

    private final Map<String, Language> languages;

    private LanguageMaster() {
        String languageFolder = FileParsingUtility.getLanguageFolder();

        this.languages = new JsonLanguageLoader().loadAll(Path.of(languageFolder));
    }

    public static LanguageMaster instance() {
        if (instance == null) {
            instance = new LanguageMaster();
        }

        return instance;
    }

    public boolean isLanguageKnown(String language) {
        return languages.containsKey(language);
    }

    public String get(String key, String language) {
        if (!isLanguageKnown(language)) {
            throw new IllegalArgumentException("Unknown language: " + language);
        }

        return languages.get(language).get(key);
    }
}
