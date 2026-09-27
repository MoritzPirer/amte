package Controller.Modes;

import Controller.Action.Action;
import Controller.Action.NotifyAction;
import Controller.Action.NullAction;
import Controller.TemporaryMessages.InputCountDuration;
import Controller.TemporaryMessages.Message;
import Controller.TemporaryMessages.MessageType;
import Debugging.DebugLogger;

record ParseSnapshot(ParseStatus status, Action action) {
    public static ParseSnapshot makeUnfinished(String progress) {
        DebugLogger.info("PARSING", "Chord unfinished, currently: " + progress);
        return new ParseSnapshot(ParseStatus.UNFINISHED, new NotifyAction(new Message(
        progress + "_",
            MessageType.INFO,
            new InputCountDuration(1)
        )));
    }

    public static ParseSnapshot INVALID = new ParseSnapshot(ParseStatus.INVALID, new NullAction());
    public static ParseSnapshot FINISHED_NO_OP = new ParseSnapshot(ParseStatus.FINISHED, new NullAction());
}
