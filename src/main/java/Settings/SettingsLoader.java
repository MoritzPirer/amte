package Settings;

import java.nio.file.Path;

interface SettingsLoader {
    SettingStorage load(Path path);
    void store(Path path, SettingStorage settings);
}
