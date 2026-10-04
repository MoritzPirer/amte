package Controller.TemporaryMessages;

import Controller.View.Line;
import Controller.View.Style;
import Controller.View.TextSpan;
import Controller.View.ViewComponent;
import Shared.Size;
import Shared.Utility.StringUtility;

import java.util.ArrayList;
import java.util.List;

public class TemporaryMessageManager {
    private final List<Message> messages;
    private int width;

    public TemporaryMessageManager() {
        messages = new ArrayList<>();
    }

    /**
     * add a new message
     * @param message the message to add
     */
    public void add(Message message) {
        messages.add(message);
    }



    /**
     * call after user input to remove input count-based messages
     */
    public void acknowledgeInput() {
        messages.removeIf(message -> message.duration().shouldRemove(true));
    }

    /**
     * call periodically to remove time-based messages
     */
    public void tick() {
        messages.removeIf(message -> message.duration().shouldRemove(false));
    }

    /**
     * change the width the messages will be formatted to when get() is called
     */
    public void setWidth(int newWidth) {
        this.width = newWidth;
    }

    public ViewComponent get() {
        List<Line> result = new ArrayList<>();
        for (Message message : messages) {
            Style style = switch (message.type()) {
                case INFO -> Style.TEXT_STYLE;
                case ERROR -> Style.ERROR;
            };

            List<String> lines = StringUtility.splitByLength(message.content(), width);
            for (String line : lines) {
                TextSpan span = new TextSpan(style, line);
                Line current = new Line(new TextSpan[]{span});
                result.add(current);
            }
        }

        Size size = new Size(result.size(), width);

        return new ViewComponent(size, result.toArray(Line[]::new));
    }
}
