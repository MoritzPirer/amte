package Settings;

public class NullConstraint<T> implements SettingConstraint<T> {
    //avoid repeated overhead by reusing stateless class
    public static final NullConstraint<Boolean> BOOLEAN = new NullConstraint<>();
    public static final NullConstraint<String> STRING = new NullConstraint<>();

    @Override
    public boolean isAllowed(T value) {
        return true;
    }
}
