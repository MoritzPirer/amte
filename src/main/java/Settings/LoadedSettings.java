package Settings;

import java.util.HashMap;
import java.util.Map;

public class LoadedSettings implements SettingStorage {
    private final Map<String, Boolean> booleanSettings;
    private final Map<String, Integer> integerSettings;
    private final Map<String, RestrictedValue<Integer>> restrictedIntegerSettings;

    private final Map<String, String> stringSettings;
    private final Map<String, RestrictedValue<String>> restrictedStringSettings;

    private final SettingStorage fallback;

    LoadedSettings(
        Map<String, Boolean> booleanSettings,
        Map<String, Integer> integerSettings,
        Map<String, RestrictedValue<Integer>> restrictedIntegerSettings,
        Map<String, String> stringSettings,
        Map<String, RestrictedValue<String>> restrictedStringSettings,
        SettingStorage fallback) {
        this.booleanSettings = booleanSettings;
        this.integerSettings = integerSettings;
        this.restrictedIntegerSettings = restrictedIntegerSettings;
        this.stringSettings = stringSettings;
        this.restrictedStringSettings = restrictedStringSettings;
        this.fallback = fallback;
    }

    @Override
    public boolean getBoolean(String key) {
        if (booleanSettings.containsKey(key)) {
            return booleanSettings.get(key);
        }

        System.out.println("falling back for boolean setting " + key);
        return fallback.getBoolean(key);
    }

    @Override
    public int getInt(String key) {
        if (integerSettings.containsKey(key)) {
            return integerSettings.get(key);
        }
        if (restrictedIntegerSettings.containsKey(key)) {
            return restrictedIntegerSettings.get(key).get();
        }

        System.out.println("falling back for int setting " + key);
        return fallback.getInt(key);
    }

    @Override
    public String getString(String key) {
        if (stringSettings.containsKey(key)) {
            return stringSettings.get(key);
        }
        if (restrictedStringSettings.containsKey(key)) {
            return restrictedStringSettings.get(key).get();
        }

        System.out.println("falling back for string setting " + key);
        return fallback.getString(key);
    }
}
