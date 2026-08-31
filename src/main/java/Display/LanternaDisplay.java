package Display;

import ColorTheme.ColorMaster;
import ColorTheme.ColorSetup;
import Controller.Controller.Controller;
import Controller.Input;
import Controller.SpecialInput;
import Controller.SizeConfiguration;
import Controller.View.*;
import Settings.SettingsMaster;
import Shared.Size;
import Shared.TextPosition;
import Shared.Utility.MathUtility;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

import java.awt.*;
import java.io.IOException;

public class LanternaDisplay extends Display {
    private static final int UI_PANEL_HEIGHT = 3;
    private static final int NUMBER_PANEL_WIDTH = 5;
    private static final TextColor CURSOR_BACKGROUND = new TextColor.RGB(255, 255, 255);
    private static final TextColor CURSOR_FOREGROUND = new TextColor.RGB(0, 0, 0);

    private TextPosition textAreaTopLeft;

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

        screenSize = new Size(newSize.getRows(), newSize.getColumns());
        return divideScreen(screenSize);
    }

    private SizeConfiguration divideScreen(Size screenSize) {
        Size uiPanelSize = new Size(UI_PANEL_HEIGHT, screenSize.width());

        Size numberPanelSize;
        if (SettingsMaster.instance().getBoolean("showNumbers")) {
            numberPanelSize = new Size(screenSize.height() - uiPanelSize.height(), NUMBER_PANEL_WIDTH);
            textAreaTopLeft = new TextPosition(0, NUMBER_PANEL_WIDTH);
        }
        else {
            numberPanelSize = new Size(0, 0);
            textAreaTopLeft = new TextPosition(0, 0);
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

        if (keyStroke.getKeyType() == KeyType.Character) {
            return new Input(keyStroke.getCharacter());
        }

        SpecialInput input = switch (keyStroke.getKeyType()) {
            case ArrowUp ->  SpecialInput.ARROW_UP;
            case ArrowDown ->  SpecialInput.ARROW_DOWN;
            case ArrowLeft ->  SpecialInput.ARROW_LEFT;
            case ArrowRight ->  SpecialInput.ARROW_RIGHT;

            case Enter -> SpecialInput.ENTER;
            case Backspace -> SpecialInput.BACK_SPACE;
            case Tab -> SpecialInput.TAB;
            case ReverseTab -> SpecialInput.SHIFT_TAB;
            case Escape -> SpecialInput.ESCAPE;
            default -> null;
        };

        if (input != null) {
            return new Input(input);
        }
        
        return null;
    }

    @Override
    protected void render(View view) throws IOException {
        screen.clear();

        renderComponent(view.numberPanel(), new TextPosition(0, 0), "numberPanel");
        renderComponent(view.textArea(), textAreaTopLeft, "textArea");
        renderComponent(
            new ViewComponent(
                new Size(1, screenSize.width()),
                new Line[0]
            ),
            new TextPosition(screenSize.height() - UI_PANEL_HEIGHT - 1, 0),
            "uiPanel"
        );
        renderComponent(view.uiPanel(), new TextPosition(screenSize.height() - UI_PANEL_HEIGHT, 0), "uiPanel");
        renderCursor(view.cursorPositionInTextArea(), new TextPosition(0, NUMBER_PANEL_WIDTH));

        screen.refresh();
    }

    private void renderComponent(ViewComponent component, TextPosition topLeft, String defaultKey) {
        // TODO WHEN [Settings AND Rendering is implemented] DO [Refactor this function to render colorless based on user preference]

        ColorSetup defaultSetup = ColorMaster.getInstance().get(defaultKey);
        TextColor defaultForeground = hexToTextColor(defaultSetup.foreground());
        TextColor defaultBackground = hexToTextColor(defaultSetup.background());

        for (int i = 0; i < component.size().height(); i++) {
            int screenRow = topLeft.paragraph() + i;
            graphics.setForegroundColor(defaultForeground);
            graphics.setBackgroundColor(defaultBackground);
            graphics.putString(topLeft.character(), screenRow, " ".repeat(component.size().width()));

            if (i >= component.lines().length) {
                continue;
            }

            Line line = component.lines()[i];
            int screenColumn = topLeft.character();

            for (TextSpan span : line.textSpans()) {
                applyStyle(span.style());
                graphics.putString(screenColumn, screenRow, span.text());
                graphics.setForegroundColor(defaultForeground);
                graphics.setBackgroundColor(defaultBackground);
                screenColumn += span.text().length();
            }
        }
    }

    private TextColor hexToTextColor(String hex) {
        int red = Integer.parseInt(hex.substring(0, 2), 16);
        int green = Integer.parseInt(hex.substring(2, 4), 16);
        int blue = Integer.parseInt(hex.substring(4, 6), 16);

        return new TextColor.RGB(red, green, blue);
    }

    private void applyStyle(Style style) {
        graphics.clearModifiers();

        ColorSetup setup = ColorMaster.getInstance().get(style.renderStyle().toString());

        if (style.isBold() || setup.isBold()) {
            graphics.enableModifiers(SGR.BOLD);
        }
        if (style.isItalic() || setup.isItalic()) {
            graphics.enableModifiers(SGR.ITALIC);
        }

        String foregroundString = setup.foreground();
        if (!foregroundString.isEmpty()) {
            Color foregroundColor = MathUtility.hexToColor(setup.foreground());
            TextColor foregroundTextColor = new TextColor.RGB(foregroundColor.getRed(), foregroundColor.getGreen(), foregroundColor.getBlue());
            graphics.setForegroundColor(foregroundTextColor);
        }

        String backgroundString = setup.background();
        if (!backgroundString.isEmpty()) {
            Color backgroundColor = MathUtility.hexToColor(setup.background());
            TextColor backgroundTextColor = new TextColor.RGB(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());
            graphics.setBackgroundColor(backgroundTextColor);
        }

    }

    /**
     * draws the cursor on the screen
     * @param visualCursorPosition the position of the cursor, relative to topLeft
     * @param topLeft the top left corner of the text area
     */
    private void renderCursor(TextPosition visualCursorPosition, TextPosition topLeft) {
        TextPosition absolute = new TextPosition(
            visualCursorPosition.paragraph() + topLeft.paragraph(),
            visualCursorPosition.character() + topLeft.character()
        );

        TextCharacter character = screen.getBackCharacter(absolute.character(), absolute.paragraph()); // read from second buffer

        graphics.setForegroundColor(CURSOR_FOREGROUND);
        graphics.setBackgroundColor(CURSOR_BACKGROUND);

        if (character.isBold()) {
            graphics.enableModifiers(SGR.BOLD);
        }

        if (character.isItalic()) {
            graphics.enableModifiers(SGR.ITALIC);
        }

        graphics.putString(
                absolute.character(),
                absolute.paragraph(),
                character.getCharacterString()
        );
    }
}
