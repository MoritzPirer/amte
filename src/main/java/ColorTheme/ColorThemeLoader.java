package ColorTheme;

import java.nio.file.Path;

interface ColorThemeLoader {
    /**
     * load the color theme from the given file path
     * @param path the path to read from
     * @return the constructed ColorTheme. If path does not exist, or the corresponding file cannot be read, an empty
     * color theme is returned.
     */
    ColorTheme load(Path path);
}
