package Settings;

import java.util.HashMap;
import java.util.Map;

public class LoadedSettings implements SettingStorage {
    private final Map<String, Boolean> booleanSettings;
    private final Map<String, Integer> integerSettings;
    private final Map<String, RestrictedValue<Integer>> restrictedIntegerSettings;

    private final Map<String, String> stringSettings;
    private final SettingStorage fallback;

    LoadedSettings(
        Map<String, Boolean> booleanSettings,
        Map<String, Integer> integerSettings,
        Map<String, RestrictedValue<Integer>> restrictedIntegerSettings,
        Map<String, String> stringSettings,
        SettingStorage fallback) {
        this.booleanSettings = booleanSettings;
        this.integerSettings = integerSettings;
        this.restrictedIntegerSettings = restrictedIntegerSettings;
        this.stringSettings = stringSettings;
        this.fallback = fallback;
    }

    @Override
    public boolean getBoolean(String key) {
        if (booleanSettings.containsKey(key)) {
            return booleanSettings.get(key);
        }

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

        return fallback.getInt(key);
    }

    @Override
    public String getString(String key) {
        if (stringSettings.containsKey(key)) {
            return stringSettings.get(key);
        }
        /*
        if (restrictedStringSettings.containsKey(key)) {
            return restrictedStringSettings.get(key).get();
        }
        */

        return fallback.getString(key);
    }
}
