package ColorTheme;

import java.awt.*;
import java.util.Map;

public class ColorTheme {
    private final Map<String, ColorSetup> colors;
    private static final ColorSetup defaultSetup = new ColorSetup("", "FFFFFF", "000000", false, false);

    public ColorTheme(Map<String, ColorSetup> colors) {
        this.colors = colors;
    }

    public ColorSetup get(String key) {
        if (colors.containsKey(key)) {
            return colors.get(key);
        }

        return defaultSetup;
    }
}
