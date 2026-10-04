package Controller.TemporaryMessages;

public interface MessageDuration {
    /**
     * check if the associated message should be removed
     * @param inputHappened true if this check is done in response to an input, false if the check
     *                      is done independent of input
     * @return true if the associated message should be removed
     */
    boolean shouldRemove(boolean inputHappened);
}
