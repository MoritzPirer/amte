package LanguageSupport;

import java.util.Map;

class Language {
    private static final boolean THROW_ON_UNKNOWN_KEY = false;

    private final Map<String, String> texts;

    public Language(Map<String, String> texts) {
        this.texts = texts;
    }

    public String get(String key) {
        if (texts.containsKey(key)) {
            return texts.get(key);
        }
        else if (THROW_ON_UNKNOWN_KEY) {
            throw new IllegalArgumentException("Unknown key: " + key);
        }

        return "<missing entry>";
    }
}
