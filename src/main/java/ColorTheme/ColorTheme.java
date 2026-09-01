package ColorTheme;

import Shared.Utility.MathUtility;

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

    ColorTheme(List<LoadedColorSetup> colors) {
        this.colors = new HashMap<>();
        for (LoadedColorSetup color : colors) {
            Color foreground = MathUtility.hexToColor(color.foreground());
            Color background = MathUtility.hexToColor(color.background());

            this.colors.put(
                color.key(),
                new ColorSetup(color.key(),
                    foreground,
                    background,
                    color.isItalic(),
                    color.isBold()
                )
            );
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
