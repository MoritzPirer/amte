package LanguageSupport;

import Settings.SettingsMaster;

public class LanguageProvider {
    private final String language;

    public LanguageProvider() {
        this.language = SettingsMaster.instance().getString("activeLanguage");
    }

    public LanguageProvider(String language) {
        if (!LanguageMaster.instance().isLanguageKnown(language)) {
            throw new IllegalArgumentException("Unknown language: " + language);
        }

        this.language = language;
    }

    public String get(String key) {
        return LanguageMaster.instance().get(key, language);
    }
}
