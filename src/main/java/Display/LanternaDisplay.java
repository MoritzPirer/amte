package Display;

import Controller.Controller;
import Controller.Input;
import Controller.SizeConfiguration;
import Controller.View.*;
import Shared.Size;
import Shared.TextPosition;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

import java.awt.*;
import java.io.IOException;

public class LanternaDisplay extends Display {
    private static final int UI_PANEL_HEIGHT = 5;
    private static final int NUMBER_PANEL_WIDTH = 5;

    private Screen screen = null;
    private TextGraphics graphics = null;
    Size screenSize;

    LanternaDisplay(Controller controller) {
        super(controller);
    }

    @Override
    protected void init() throws IOException {
        assert screen == null: "Double Initialization!";

        Font font = new Font("Courier New", Font.PLAIN, 12);
        SwingTerminalFontConfiguration c = SwingTerminalFontConfiguration.newInstance(font);
        this.screen = new DefaultTerminalFactory()
            //.setInitialTerminalSize(new TerminalSize(60, 9))
            .setTerminalEmulatorTitle("strato")
            .setTerminalEmulatorFontConfiguration(c)
            .createScreen();

        this.screen.startScreen();
        this.screen.setCursorPosition(null);
        this.graphics = screen.newTextGraphics()
            .setBackgroundColor(new TextColor.RGB(0, 0, 0))
            .setForegroundColor(new TextColor.RGB(255, 255, 255))
            .fill(' ');

        TerminalSize terminalSize = screen.getTerminalSize();
        this.screenSize = new Size(terminalSize.getRows(), terminalSize.getColumns());
    }

    @Override
    protected void destroy() throws IOException {
        assert screen != null: "Double Destroy or destroy before initialization!";

        screen.stopScreen();
        screen.close();
        screen = null;
    }

    @Override
    protected SizeConfiguration initialSizeConfiguration() {
        TerminalSize size = screen.getTerminalSize();
        Size screenSize = new Size(size.getRows(), size.getColumns());

        return divideScreen(screenSize);
    }
    @Override
    protected SizeConfiguration checkSizeConfiguration() {
        TerminalSize newSize = screen.doResizeIfNecessary();
        if (newSize == null) {
            return null;
        }

        // this might be replaced with more graceful handling
        if (newSize.getRows() < 5 || newSize.getColumns() < 20) {
            throw new RuntimeException("Screen too small!");
        }

        Size screenSize = new Size(newSize.getRows(), newSize.getColumns());
        return divideScreen(screenSize);
    }

    private SizeConfiguration divideScreen(Size screenSize) {
        Size uiPanelSize = new Size(UI_PANEL_HEIGHT, screenSize.height());

        Size numberPanelSize;
        boolean showNumbers = true; //TODO connect with settings
        if (showNumbers) {
            numberPanelSize = new Size(screenSize.height() - uiPanelSize.height(), NUMBER_PANEL_WIDTH);
        }
        else {
            numberPanelSize = new Size(0, 0);
        }

        Size textAreaSize = new Size(screenSize.height() - uiPanelSize.height(), screenSize.width() - numberPanelSize.width());

        return new SizeConfiguration(screenSize, textAreaSize, uiPanelSize, numberPanelSize);
    }

    @Override
    protected Input checkInput() throws IOException {
        KeyStroke keyStroke = screen.pollInput();

        if (keyStroke == null) {
            return null;
        }

        //TODO!!!
        return new Input('q');
    }

    @Override
    protected void render(View view) throws IOException {
        screen.clear();
        renderComponent(view.numberPanel(), new TextPosition(0, 0), null);
        renderComponent(view.textArea(), new TextPosition(0, NUMBER_PANEL_WIDTH), null);
        renderComponent(view.uiPanel(), new TextPosition(screenSize.height() - UI_PANEL_HEIGHT, 0), new TextColor.RGB(128, 128, 128));
        screen.refresh();
    }

    private void renderComponent(ViewComponent component, TextPosition topLeft, TextColor backgroundColor) {
        // TODO account for no-color setting later on

        for (int i = 0; i < component.size().height(); i++) {
            int screenRow = topLeft.paragraph() + i;
            if (backgroundColor != null) {
                graphics.setBackgroundColor(backgroundColor);
                graphics.putString(topLeft.character(), screenRow, " ".repeat(component.size().width()));
            }

            Line line = component.lines()[i];
            int screenColumn = topLeft.character();

            for (TextSpan span : line.textSpans()) {
                applyStyle(span.style(), backgroundColor);
                graphics.putString(screenColumn, screenRow, span.text());
                screenColumn += span.text().length();
            }
        }
    }

    private void applyStyle(Style style, TextColor customBackgroundColor) {
        graphics.clearModifiers();

        if (style.isBold()) {
            graphics.enableModifiers(SGR.BOLD);
        }
        if (style.isItalic()) {
            graphics.enableModifiers(SGR.ITALIC);
        }

        // this will change when getForegroundColor / getBackgroundColor change
        Color foregroundColor = getForegroundColor(style.renderStyle());
        TextColor foregroundTextColor = new TextColor.RGB(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue());
        graphics.setForegroundColor(foregroundTextColor);

        if (customBackgroundColor != null) {
            graphics.setBackgroundColor(customBackgroundColor);
        }
        else  {
            Color backgroundColor = getBackgroundColor(style.renderStyle());
            TextColor backgroundTextColor = new TextColor.RGB(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
            graphics.setBackgroundColor(backgroundTextColor);
        }
    }

    /**
     * this will eventually be controlled via color objects defined by color theme settings (either force reload or restart needed)
     */
    private Color getForegroundColor(RenderStyle style) {
        return switch (style) {
            case TEXT -> Color.white;
            case WEAK_HIGHLIGHT -> Color.cyan;
            case MEDIUM_HIGHLIGHT -> Color.magenta;
            case STRONG_HIGHLIGHT -> Color.WHITE;
            case FILE_CREATED -> Color.RED;
            case FILE_MODIFIED -> Color.orange;
            case FILE_SAVED -> Color.green;
            case CODE_BUILTIN -> Color.cyan;
            case CODE_KEYWORD -> Color.red;
            case CODE_STRING_LITERAL -> Color.blue;
            case CODE_NUMBER_LITERAL -> Color.yellow;
            case CODE_COMMENT -> Color.gray;
        };
    }

    /**
     * this will eventually be controlled via color objects defined by color theme settings (either force reload or restart needed)
     */
    private Color getBackgroundColor(RenderStyle style) {
        return switch (style) {
            case CODE_BUILTIN, CODE_COMMENT, CODE_NUMBER_LITERAL, CODE_STRING_LITERAL, CODE_KEYWORD -> Color.darkGray;
            case STRONG_HIGHLIGHT -> Color.cyan;
            default -> Color.black;
        };
    }
}
