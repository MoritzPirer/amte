package Settings;

interface SettingConstraint<T> {
    boolean isAllowed(T value);
}
