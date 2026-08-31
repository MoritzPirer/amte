package Settings;

import Shared.Utility.FileParsingUtility;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingsMaster {
    private static SettingsMaster instance = null;
    private final SettingStorage storage;

    private SettingsMaster() {
        String baseFolder = FileParsingUtility.getBaseFolder();
        String settingsPath = baseFolder + File.separator + "settings.json";

        Path path = Paths.get(settingsPath);
        this.storage = new JsonSettingsLoader().load(path);
    }

    public static SettingsMaster instance() {
       if (instance == null) {
           instance = new SettingsMaster();
       }

       return instance;
    }

    public boolean getBoolean(String key) {
        return storage.getBoolean(key);
    }

    public int getInt(String key) {
        return storage.getInt(key);
    }

    public String getString(String key) {
        return storage.getString(key);
    }
}
