package Settings;

record Setting<T>(T value, SettingConstraint<T> constraint) { }
