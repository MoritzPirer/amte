package Controller.TemporaryMessages;

/**
 * requests removal after at least a set amount of time has passed
 */
public class TimeDuration implements MessageDuration {
    private final long startTime;
    private final int durationMillis;

    private TimeDuration(int durationMillis) {
        this.startTime = System.currentTimeMillis();
        this.durationMillis = durationMillis;
    }

    public static TimeDuration seconds(int durationSeconds) {
        return new TimeDuration(durationSeconds * 1000);
    }

    @Override
    public boolean shouldRemove(boolean inputHappened) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - startTime;

        return elapsedTime >= durationMillis;
    }
}
