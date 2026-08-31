package Settings;

import java.util.HashMap;
import java.util.Map;

class DefaultSettings implements SettingStorage {
    private final Map<String, Boolean> booleanSettings;
    private final Map<String, Integer> intSettings = new HashMap<>();
    private final Map<String, RestrictedValue<Integer>> restrictedIntSettings;
    private final Map<String, String> stringSettings;
    private final Map<String, RestrictedValue<String>> restrictedStringSettings = new HashMap<>();

    public DefaultSettings() {
        booleanSettings = new HashMap<>();
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

        restrictedIntSettings = new HashMap<>();
        restrictedIntSettings.put("tabWidth", new RestrictedValue<>(4, new Integer[]{2, 4}));

        stringSettings = new HashMap<>();
        stringSettings.put("activeColorTheme", "default.json");

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
        if (intSettings.containsKey(key)) {
            return intSettings.get(key);
        }
        if (restrictedIntSettings.containsKey(key)) {
            return restrictedIntSettings.get(key).get();
        }

        throw new IllegalArgumentException("No such key: " + key);
    }

    @Override
    public String getString(String key) {
        if (stringSettings.containsKey(key)) {
            return stringSettings.get(key);
        }
        if (restrictedStringSettings.containsKey(key)) {
            return restrictedStringSettings.get(key).get();
        }

        throw new IllegalArgumentException("No such key: " + key);
    }
}
