package Settings;

public class SuffixConstraint implements SettingConstraint<String> {
    private final String suffix;

    public SuffixConstraint(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public boolean isAllowed(String value) {
        return value.endsWith(suffix);
    }
}
