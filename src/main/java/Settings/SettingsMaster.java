package Settings;

public class SettingsMaster {
    private static SettingsMaster instance = null;
    private final SettingStorage storage;

    private SettingsMaster() {
        this.storage = new DefaultSettings(); //TEMP
        //TODO file-loaded Settings
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
