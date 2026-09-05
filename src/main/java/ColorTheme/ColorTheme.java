package ColorTheme;

import Shared.Utility.NumberUtility;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorTheme {
    private final Map<String, ColorSetup> colors;
    private static final ColorSetup defaultSetup = new ColorSetup(
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

        System.err.println("falling back to default for unknown key " + key);
        return defaultSetup;
    }
}
