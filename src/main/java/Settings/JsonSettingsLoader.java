package Settings;

import Debugging.DebugLogger;
import Shared.Utility.FileParsingUtility;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

class JsonSettingsLoader implements SettingsLoader {

    private final DefaultSettings defaultSettings = new DefaultSettings();

    public record SettingItem(String key, String type, String value) { }

    @Override
    public SettingStorage load(Path path) {

        if (!Files.exists(path)) {
            return defaultSettings;
        }

        String jsonString;
        try {
            jsonString = FileParsingUtility.readFile(path);
        }
        catch (IOException e) {
            return defaultSettings;
        }

        String[] items = FileParsingUtility.splitIntoItems(jsonString);

        Map<String, Boolean> booleanSettings = new HashMap<>();
        Map<String, Integer> intSettings = new HashMap<>();
        Map<String, String> stringSettings = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper();
        for (String itemString : items) {
            try {
                SettingItem item = mapper.readValue(itemString, SettingItem.class);
                addToMap(item, booleanSettings, intSettings, stringSettings);
            }
            catch (JacksonException ignored) { /* skip invalid settings */ }
        }

        return new LoadedSettings(booleanSettings, intSettings, stringSettings, new DefaultSettings());
    }

    private void addToMap(
        SettingItem item,
        Map<String, Boolean> booleanSettings,
        Map<String, Integer> intSettings,
        Map<String, String> stringSettings
    ) {
        switch (item.type().toLowerCase()) {
            case "boolean" -> addBoolean(booleanSettings, item);
            case "integer" -> addInteger(intSettings, item);
            case "string" -> addString(stringSettings, item);
            default -> DebugLogger.warn("SETTINGS", "ignoring unknown setting type " + item.type()
                + " requested by " + item.key());
        }
    }

    private void addBoolean(Map<String, Boolean> map, SettingItem item) {
        boolean value = Boolean.parseBoolean(item.value());

        if (!defaultSettings.doesBooleanSettingExist(item.key())) {
            DebugLogger.warn("SETTINGS", "Ignoring unknown setting '" + item.key() + "'!");
            return;
        }

        map.put(item.key(), value);
    }

    private void addInteger(Map<String, Integer> map, SettingItem item) {
        int value;

        try {
            value = Integer.parseInt(item.value());
        }
        catch (NumberFormatException ignored) {
            DebugLogger.warn("SETTINGS", "Ignoring non-integer value'" + item.value()
                + "' for setting '" + item.key() + "'!");
            return;
        }

        if (!defaultSettings.doesIntegerSettingExist(item.key)) {
            DebugLogger.warn("SETTINGS", "Ignoring unknown setting '" + item.key() + "'!");
            return;
        }
        if (defaultSettings.isIntegerAllowed(item.key(), value)) {
            map.put(item.key(), value);
        }
        else {
            DebugLogger.warn("SETTINGS", "Invalid setting value '" + value
                + "' for setting '" + item.key() + "'; falling back to default...");

            map.put(item.key(), defaultSettings.getInt(item.key()));
        }
    }

    private void addString(Map<String, String> map, SettingItem item) {
        String value = item.value();

        if (!defaultSettings.doesStringSettingExist(item.key())) {
            DebugLogger.warn("SETTINGS", "Ignoring unknown setting '" + item.key() + "'!");
            return;
        }
        if (defaultSettings.isStringAllowed(item.key(), value)) {
            map.put(item.key(), value);
        }
        else {
            DebugLogger.warn("SETTINGS", "Invalid setting value '" + value
                + "' for setting '" + item.key() + "'; falling back to default...");

            map.put(item.key(), defaultSettings.getString(item.key()));
        }
    }

    @Override
    public void store(Path path, SettingStorage settings) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
