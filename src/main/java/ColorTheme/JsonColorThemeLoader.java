package ColorTheme;

import Shared.Utility.FileParsingUtility;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JsonColorThemeLoader implements ColorThemeLoader {

    @Override
    public ColorTheme load(Path path) {
        if (!Files.exists(path)) {
            return new ColorTheme(new HashMap<>());
        }

        String[] items;
        try {
            String jsonString = FileParsingUtility.readFile(path);
            items = FileParsingUtility.splitIntoItems(jsonString);
        }
        catch (IOException e) {
            return new ColorTheme(new HashMap<>());
        }

        Map<String, ColorSetup> colors = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        for (String itemString : items) {
            try {
                ColorSetup item = mapper.readValue(itemString, ColorSetup.class);
                colors.put(item.key(), item);
            }
            catch (JacksonException ignored) {
            }
        }

        return new ColorTheme(colors);
    }
}
