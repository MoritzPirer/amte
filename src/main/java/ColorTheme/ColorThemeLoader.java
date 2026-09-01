package ColorTheme;

import java.nio.file.Path;

interface ColorThemeLoader {
    ColorTheme load(Path path);
}
