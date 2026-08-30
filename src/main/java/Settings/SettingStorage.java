package Settings;

interface SettingStorage {
    boolean getBoolean(String key);
    int getInt(String key);
    String getString(String key);
}
