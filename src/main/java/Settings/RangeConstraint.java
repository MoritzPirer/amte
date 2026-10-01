package Settings;

public class RangeConstraint implements SettingConstraint<Integer> {
    private final Integer smallestAllowed;
    private final Integer largestAllowed;

    /**
     *
     * @param smallestAllowed inclusive lower bound
     * @param largestAllowed inclusive upper bound
     */
    private RangeConstraint(Integer smallestAllowed, Integer largestAllowed) {
        this.smallestAllowed = smallestAllowed;
        this.largestAllowed = largestAllowed;
    }

    public static RangeConstraint between(int lower, int upper) {
        if (lower > upper) {
            throw new IllegalArgumentException("lower should be less than upper");
        }

        return new RangeConstraint(lower, upper);
    }

    public static RangeConstraint atLeast(int smallest) {
        return new RangeConstraint(smallest, null);
    }

    public static RangeConstraint atMost(int largest) {
        return new RangeConstraint(null, largest);
    }

    @Override
    public boolean isAllowed(Integer value) {
        if (smallestAllowed == null) {
            return value <= largestAllowed;
        }
        if (largestAllowed == null) {
            return value >= smallestAllowed;
        }

        return value >= smallestAllowed && value <= largestAllowed;
    }
}
