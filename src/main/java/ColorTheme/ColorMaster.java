package ColorTheme;

import Settings.SettingsMaster;
import Shared.Utility.FileParsingUtility;

import java.nio.file.Path;

public class ColorMaster {
    private static ColorMaster instance = null;

    private final ColorTheme theme;
    private ColorMaster() {
        String colorThemePath = FileParsingUtility.getColorThemesFolder()
            + SettingsMaster.instance().getString("activeColorTheme");
        theme = new JsonColorThemeLoader().load(Path.of(colorThemePath));
    }

    public static ColorMaster instance() {
        if (instance == null) {
            instance = new ColorMaster();
        }

        return instance;
    }

    public ColorSetup get(String key) {
        return theme.get(key);
    }
}
