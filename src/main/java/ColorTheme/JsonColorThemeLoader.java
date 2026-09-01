package ColorTheme;

import Shared.Utility.FileParsingUtility;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class JsonColorThemeLoader implements ColorThemeLoader {

    @Override
    public ColorTheme load(Path path) {
        if (!Files.exists(path)) {
            return new ColorTheme(new ArrayList<LoadedColorSetup>());
        }

        String[] items;
        try {
            String jsonString = FileParsingUtility.readFile(path);
            items = FileParsingUtility.splitIntoItems(jsonString);
        }
        catch (IOException e) {
            return new ColorTheme(new ArrayList<LoadedColorSetup>());
        }

        List<LoadedColorSetup> colors = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (String itemString : items) {
            try {
                LoadedColorSetup item = mapper.readValue(itemString, LoadedColorSetup.class);
                colors.add(item);
            }
            catch (JacksonException ignored) {
            }
        }

        return new ColorTheme(colors);
    }
}
