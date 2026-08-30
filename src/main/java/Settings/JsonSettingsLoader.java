package Settings;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

        String jsonString = readFile(path);
        String[] items = splitIntoItems(jsonString);

        Map<String, Boolean> booleanSettings = new HashMap<>();
        Map<String, Integer> intSettings = new HashMap<>();
        Map<String, RestrictedValue<Integer>> restrictedIntSettings = new HashMap<>();

        ObjectMapper objectMapper = new ObjectMapper();
        for (String itemString : items) {
            try {
                SettingItem item = objectMapper.readValue(itemString, SettingItem.class);
                addToMap(item, booleanSettings, intSettings, restrictedIntSettings);
            }
            catch (JacksonException ignored) {
                //skip invalid settings
            }
        }

        return new LoadedSettings(
                booleanSettings,
                intSettings,
                restrictedIntSettings,
                new DefaultSettings()
        );
    }

    private String readFile(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            StringBuilder builder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            return builder.toString();
        }
        catch (IOException e) {
            return null;
        }
    }

    private String[] splitIntoItems(String jsonString) {
        jsonString = jsonString.substring(1, jsonString.length() - 1); // remove outer brackets

        //split at every "},{", but only consume the comma
        return jsonString.split("(?<=\\{)\\s*,\\s*(?=\\{)");
    }

    private void addToMap(
        SettingItem item,
        Map<String, Boolean> booleanSettings,
        Map<String, Integer> intSettings,
        Map<String, RestrictedValue<Integer>> restrictedIntSettings) {

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
                default -> {}
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

    @Override
    public void store(Path path, SettingStorage settings) {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
