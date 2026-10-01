package Settings;

public class WhiteListConstraint<T> implements SettingConstraint<T> {
    private final T[] whiteList;

    @SafeVarargs
    public WhiteListConstraint(T... whiteList) {
        this.whiteList = whiteList;
    }

    @Override
    public boolean isAllowed(T value) {
        for (T whiteListElement : whiteList) {
            if (whiteListElement.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
