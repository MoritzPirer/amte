package Controller.TemporaryMessages;

/**
 * requests removal after a set number of checks if the message should be removed
 */
public class InputCountDuration implements MessageDuration {
    private int count;

    public InputCountDuration(int count) {
        this.count = count;
    }

    @Override
    public boolean shouldRemove() {
        return --count >= 0;
    }
}
