package Settings;

import java.util.HashMap;
import java.util.Map;

class DefaultSettings implements SettingStorage {
    private final Map<String, Boolean> booleanSettings = new HashMap<>();
    private final Map<String, Setting<Integer>> intSettings = new HashMap<>();
    private final Map<String, Setting<String>> stringSettings = new HashMap<>();

    public DefaultSettings() {
        booleanSettings.put("showNumbers", true);
        booleanSettings.put("doRelativeNumbers", false);
        booleanSettings.put("doColoredRendering", true);
        booleanSettings.put("showFileName", true);
        booleanSettings.put("showSaveStateWord", true);
        booleanSettings.put("showSaveStateIcon", true);
        booleanSettings.put("showCursorPosition", true);
        booleanSettings.put("showCharacterCount", true);
        booleanSettings.put("showWordCount", true);
        booleanSettings.put("showParagraphCount", true);
        booleanSettings.put("copyOnDelete", true);
        booleanSettings.put("yankToSystemClipboard", true);
        booleanSettings.put("doSpellCheck", true);

        intSettings.put("maxCorrections", new Setting<>(5, RangeConstraint.between(1, 9)));
        intSettings.put("tabWidth", new Setting<>(4, new WhiteListConstraint<>(2, 4)));

        stringSettings.put("activeColorTheme", new Setting<>("default.json", new SuffixConstraint(".json")));
        stringSettings.put("activeLanguage", new Setting<>("english", new NullConstraint<>()));
        stringSettings.put("spellCheckLanguage", new Setting<>("english", new NullConstraint<>()));
        stringSettings.put("numberSeparator", new Setting<>(".", new WhiteListConstraint<>(" ", ",", ".", "_")));
    }

    @Override
    public boolean getBoolean(String key) {
        if (booleanSettings.containsKey(key)) {
            return booleanSettings.get(key);
        }

        throw new IllegalArgumentException("No such key: " + key);
    }

    @Override
    public int getInt(String key) {
        return get(intSettings, key);
    }

    @Override
    public String getString(String key) {
        return get(stringSettings, key);
    }

    private <T> T get(Map<String, Setting<T>> container, String key) {
        if (container.containsKey(key)) {
            return container.get(key).value();
        }
        throw new IllegalArgumentException("No such key: " + key);
    }

    public boolean isStringAllowed(String key, String value) {
        return isAllowed(key, stringSettings, value);
    }

    public boolean isIntegerAllowed(String key, Integer value) {
        return isAllowed(key, intSettings, value);
    }

    private <T> boolean isAllowed(String key, Map<String, Setting<T>> container, T value) {
        if (container.containsKey(key)) {
            return container.get(key).constraint().isAllowed(value);
        }

        throw new IllegalArgumentException("No such key: " + key);
    }

    public boolean doesBooleanSettingExist(String key) {
        return booleanSettings.containsKey(key);
    }
    public boolean doesIntegerSettingExist(String key) {
        return intSettings.containsKey(key);
    }

    public boolean doesStringSettingExist(String key) {
        return stringSettings.containsKey(key);
    }
}
