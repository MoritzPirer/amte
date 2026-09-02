package Settings;

import Shared.Utility.FileParsingUtility;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class JsonSettingsLoader implements SettingsLoader {

    @Override
    public SettingStorage load(Path path) {
        if (!Files.exists(path)) {
            return new DefaultSettings();
        }

        String jsonString;
        try {
            jsonString = FileParsingUtility.readFile(path);
        }
        catch (IOException e) {
            return new DefaultSettings();
        }

        String[] items = FileParsingUtility.splitIntoItems(jsonString);

        Map<String, Boolean> booleanSettings = new HashMap<>();
        Map<String, Integer> intSettings = new HashMap<>();
        Map<String, RestrictedValue<Integer>> restrictedIntSettings = new HashMap<>();
        Map<String, String> stringSettings = new HashMap<>();
        Map<String, RestrictedValue<String>> restrictedStringSettings = new HashMap<>();

        ObjectMapper mapper = new ObjectMapper();
        for (String itemString : items) {
            try {
                SettingItem item = mapper.readValue(itemString, SettingItem.class);
                addToMap(item, booleanSettings, intSettings, restrictedIntSettings, stringSettings, restrictedStringSettings);
            }
            catch (JacksonException ignored) {
                //skip invalid settings
            }
        }

        return new LoadedSettings(
                booleanSettings,
                intSettings,
                restrictedIntSettings,
                stringSettings,
                restrictedStringSettings,
                new DefaultSettings()
        );
    }

    private void addToMap(
        SettingItem item,
        Map<String, Boolean> booleanSettings,
        Map<String, Integer> intSettings,
        Map<String, RestrictedValue<Integer>> restrictedIntSettings,
        Map<String, String> stringSettings,
        Map<String, RestrictedValue<String>> restrictedStringSettings) {

        try {
            switch (item.type()) {
                case "boolean" -> {
                    booleanSettings.put(item.key(), Boolean.parseBoolean(item.value()));
                }
                case "integer" -> {
                    intSettings.put(item.key(), Integer.parseInt(item.value()));
                }
                case "restrictedInteger" -> {
                    addToRestrictedIntegerMap(item, restrictedIntSettings);
                }
                case "string" -> {
                    stringSettings.put(item.key(), item.value());
                }
                case "restrictedString" -> {
                    addToRestrictedStringMap(item, restrictedStringSettings);
                }
                default -> {
                    System.out.println("ignoring unknown setting type " + item.type());
                }
            }
        }
        catch (NumberFormatException ignored) {
            //skip settings with bad format
        }
    }

    private void addToRestrictedIntegerMap(SettingItem item, Map<String, RestrictedValue<Integer>> restrictedIntSettings) {
        if (item.options() == null || item.options().length == 0) {
            return;
        }

        Integer value = Integer.parseInt(item.value());
        Integer[] options = Arrays.stream(item.options())
                .map(Integer::parseInt)
                .toArray(Integer[]::new);

        restrictedIntSettings.put(item.key(), new RestrictedValue<>(value, options));
    }

    private void addToRestrictedStringMap(SettingItem item, Map<String, RestrictedValue<String>> restrictedStringSettings) {
        if (item.options() == null || item.options().length == 0) {
            return;
        }
        restrictedStringSettings.put(item.key(), new RestrictedValue<>(item.value(), item.options()));
    }

    @Override
    public void store(Path path, SettingStorage settings) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
