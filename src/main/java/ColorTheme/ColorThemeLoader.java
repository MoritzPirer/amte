package ColorTheme;

import java.io.IOException;
import java.nio.file.Path;

public interface ColorThemeLoader {
    ColorTheme load(Path path);
}
