package Controller.TemporaryMessages;

import Controller.View.Line;
import Controller.View.Style;
import Controller.View.TextSpan;
import Controller.View.ViewComponent;
import Shared.Size;
import Shared.Utility.StringUtility;

import java.util.LinkedList;
import java.util.List;

public class TemporaryMessageManager {
    private final List<Message> messages;
    private int width;

    public TemporaryMessageManager() {
        messages = new LinkedList<>();
    }

    public void add(Message message) {
        messages.add(message);
    }

    public void updateWidth(int newWidth) {
        this.width = newWidth;
    }

    public void refresh() {
        messages.removeIf(message -> message.duration().shouldRemove());
    }

    public ViewComponent get() {
        List<Line> result = new LinkedList<>();
        for (Message message : messages) {
            Style style = switch (message.type()) {
                case INFO -> Style.TEXT_STYLE;
                case ERROR -> Style.ERROR;
            };

            List<String> lines = StringUtility.splitByLength(message.content(), width);
            for (String line : lines) {
                TextSpan span = new TextSpan(style, message.content());
                Line current = new Line(new TextSpan[]{span});
                result.add(current);
            }
        }

        Size size = new Size(result.size(), width);

        return new ViewComponent(size, result.toArray(Line[]::new));
    }

}
