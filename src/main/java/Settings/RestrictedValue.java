package Settings;

import java.util.Arrays;

public class RestrictedValue<T> {
    private T value;
    private final T[] options;

    public RestrictedValue(T initialValue, T[] options) {
        this.value = initialValue;
        this.options = options;
    }
    public T get() {
        return value;
    }

    public void set(T value) {
        for (T option : options) {
            if (option.equals(value)) {
                this.value = value;
                return;
            }
        }

        throw new IllegalArgumentException("Cannot set to unknown value " + value);
    }
}
