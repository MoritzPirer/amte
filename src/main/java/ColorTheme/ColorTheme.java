package ColorTheme;

import Debugging.DebugLogger;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorTheme {
    private final Map<String, ColorSetup> colors;
    private static final ColorSetup DEFAULT_SETUP = new ColorSetup(
        "",
        Color.WHITE,
        Color.BLACK,
        false,
        false
    );

    ColorTheme(List<ColorSetup> colors) {
        this.colors = new HashMap<>();
        for (ColorSetup color : colors) {
            this.colors.put(color.key(), color);
        }
    }

    public ColorSetup get(String key) {
        if (colors.containsKey(key)) {
            return colors.get(key);
        }

        DebugLogger.warn("COLOR", "falling back for unknown key " + key);
        return DEFAULT_SETUP;
    }
}
