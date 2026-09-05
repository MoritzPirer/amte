package ColorTheme;

import Shared.Utility.FileParsingUtility;
import Shared.Utility.NumberUtility;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class JsonColorThemeLoader implements ColorThemeLoader {

    private record LoadedColorSetup(String key, String foreground, String background, boolean isItalic, boolean isBold) {
        public ColorSetup convert() {
            return new ColorSetup(
                    key,
                    NumberUtility.hexToColor(foreground),
                    NumberUtility.hexToColor(background),
                    isItalic,
                    isBold
            );
        }
    }

    @Override
    public ColorTheme load(Path path) {
        if (!Files.exists(path)) {
            return new ColorTheme(new ArrayList<>());
        }

        String[] items;
        try {
            String jsonString = FileParsingUtility.readFile(path);
            items = FileParsingUtility.splitIntoItems(jsonString);
        }
        catch (IOException e) {
            return new ColorTheme(new ArrayList<>());
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

        List<ColorSetup> converted = colors.stream().map(LoadedColorSetup::convert).toList();
        return new ColorTheme(converted);
    }
}
