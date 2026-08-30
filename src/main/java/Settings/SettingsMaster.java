package Settings;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SettingsMaster {
    private static SettingsMaster instance = null;
    private final SettingStorage storage;

    private SettingsMaster() {
        String baseFolder = System.getProperty("user.home") + File.separator + ".strato/";
        ensureDirectoryExists(baseFolder);
        String settingsPath = baseFolder + File.separator + "settings.json";

        Path path = Paths.get(settingsPath);
        this.storage = new JsonSettingsLoader().load(path);
    }

    // maybe move this to a utility class
    private static void ensureDirectoryExists(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            boolean ignored = folder.mkdirs();
        }
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
}
