package Display;

import Controller.Controller.Controller;
import Controller.Input;
import Controller.SizeConfiguration;
import Controller.View.*;

public class MockController implements Controller {
    private boolean isRunning = true;
    SizeConfiguration sizeConfiguration;

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public View getView() {
        return new View(
            textComponent(),
            uiComponent(),
            numberComponent()
        );
    }

    private ViewComponent numberComponent() {
        Line[] lines = new Line[sizeConfiguration.numberingSize().height()];
        String text =  " ".repeat(sizeConfiguration.numberingSize().width() - 2) + "1 ";
        TextSpan[] spans = new TextSpan[]{new TextSpan(new Style(false, false, RenderStyle.WEAK_HIGHLIGHT), text)};
        for (int i = 0; i < lines.length; i++) {
            lines[i] = new Line(spans);
        }

        return new ViewComponent(sizeConfiguration.numberingSize(), lines);
    }

    private ViewComponent uiComponent() {
        String text = "This is part of the UI panel";

        Line[] lines = new Line[sizeConfiguration.numberingSize().height()];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = new Line(new TextSpan[]{new TextSpan(
                new Style(true, false, RenderStyle.TEXT),
                text
            )});
        }

        return new ViewComponent(sizeConfiguration.uiSize(), lines);
    }

    private ViewComponent textComponent() {
        String text = "Lorem ipsum dolor sit amet; foo bar baz bat";

        Line[] lines = new Line[sizeConfiguration.textAreaSize().height()];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = new Line(new TextSpan[]{new TextSpan(
                    new Style(false, true, RenderStyle.MEDIUM_HIGHLIGHT),
                    text
            )});
        }

        return new ViewComponent(sizeConfiguration.textAreaSize(), lines);
    }

    @Override
    public void updateSizeConfiguration(SizeConfiguration sizeConfiguration) {
        this.sizeConfiguration = sizeConfiguration;
    }

    @Override
    public void applyInput(Input input) {
        if (input.isCharacter() && input.getCharacter() == 'q') {
            isRunning = false;
        }
    }
}
