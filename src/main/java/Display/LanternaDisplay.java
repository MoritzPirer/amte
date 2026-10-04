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
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TabBehaviour;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

import java.awt.*;
import java.io.IOException;

public class LanternaDisplay extends Display {
    private static final int UI_PANEL_HEIGHT = 3;
    private static final int UI_PANEL_PADDING = 1;
    private static final int NUMBER_PANEL_WIDTH = 5;
    private static final int NUMBER_PANEL_PADDING = 1;
    private static final int TEXT_AREA_PADDING = 1;

    private static final TextColor CURSOR_BACKGROUND = new TextColor.RGB(255, 255, 255);
    private static final TextColor CURSOR_FOREGROUND = new TextColor.RGB(0, 0, 0);

    private ScreenArrangement screenArrangement;

    private Screen screen = null;
    private TextGraphics graphics = null;
    Size screenSize;

    protected LanternaDisplay(Controller controller) {
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
            .setTabBehaviour(TabBehaviour.CONVERT_TO_FOUR_SPACES)
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

        return divideScreen(screenSize, 0);
    }

    @Override
    protected SizeConfiguration checkSizeConfiguration(int recommendedNumberWidth) {
        TerminalSize newSize = screen.doResizeIfNecessary();
        if (newSize == null) {
            return null;
        }

        // this might be replaced with more graceful handling
        if (newSize.getRows() < 5 || newSize.getColumns() < 20) {
            throw new RuntimeException("Screen too small!");
        }

        screenSize = new Size(newSize.getRows(), newSize.getColumns());
        return divideScreen(screenSize, recommendedNumberWidth);
    }

    private SizeConfiguration divideScreen(Size screenSize, int recommendedNumberWidth) {
        ScreenElement uiPanel = new ScreenElement(
            new Size(UI_PANEL_HEIGHT, screenSize.width() - 2 * UI_PANEL_PADDING),
            new Size(UI_PANEL_HEIGHT + UI_PANEL_PADDING, screenSize.width()),
            new TextPosition(screenSize.height() - UI_PANEL_HEIGHT, UI_PANEL_PADDING),
            new TextPosition(screenSize.height() - UI_PANEL_HEIGHT - UI_PANEL_PADDING, 0)
        );

        ScreenElement numberPanel;
        int numberPanelHeight = screenSize.height() - uiPanel.paddedSize().height();
        if (SettingsMaster.instance().getBoolean("showNumbers")) {
            int numberPanelWidth = Math.max(recommendedNumberWidth + 2 * NUMBER_PANEL_PADDING, NUMBER_PANEL_WIDTH);
            numberPanel = new ScreenElement(
                new Size(numberPanelHeight, numberPanelWidth - 2 * NUMBER_PANEL_PADDING),
                new Size(numberPanelHeight, numberPanelWidth),
                new TextPosition(0, NUMBER_PANEL_PADDING),
                new TextPosition(0, 0)
            );
        }
        else {
            numberPanel = new ScreenElement(
                new Size(numberPanelHeight, 0),
                new Size(numberPanelHeight, 0),
                new TextPosition(0, 0),
                new TextPosition(0, 0)
            );
        }

        ScreenElement textArea = new ScreenElement(
            new Size(numberPanel.paddedSize().height(), screenSize.width() - numberPanel.paddedSize().width() - TEXT_AREA_PADDING),
            new Size(numberPanel.paddedSize().height(), screenSize.width() - numberPanel.paddedSize().width()),
            new TextPosition(0, numberPanel.paddedSize().width() + TEXT_AREA_PADDING),
            new TextPosition(0, numberPanel.paddedSize().width())
        );

        this.screenArrangement = new ScreenArrangement(textArea, numberPanel, uiPanel);

        return screenArrangement.getSizeConfiguration();
    }

    @Override
    protected Input checkInput() throws IOException {
        KeyStroke keyStroke = screen.pollInput();

        if (keyStroke == null) {
            return null;
        }

        if (keyStroke.getKeyType() == KeyType.Character) {
            if (keyStroke.isCtrlDown() && keyStroke.getCharacter() == 'v') {
                return new Input(SpecialInput.PASTE);
            }
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

        renderComponent(view.numberPanel(), screenArrangement.numberPanel(), "numberPanel");
        renderComponent(view.textArea(), screenArrangement.textArea(), "textArea");
        renderComponent(view.uiPanel(), screenArrangement.uiPanel(), "uiPanel");
        renderTemporaryMessages(view.temporaryMessages(), screenArrangement);
        renderCursor(view.cursorPositionInTextArea(), screenArrangement.textArea().contentTopLeft());

        screen.refresh();
    }

    private void renderTemporaryMessages(ViewComponent temporaryMessages, ScreenArrangement screenArrangement) {
        TextPosition temporaryMessagePosition = new TextPosition(
                screenArrangement.textArea().elementTopLeft().paragraph()
                        + screenArrangement.textArea().paddedSize().height() - temporaryMessages.size().height(),
                screenArrangement.textArea().elementTopLeft().character()
        );

        ScreenElement temporaryMessagesElement = new ScreenElement(
                temporaryMessages.size(),
                temporaryMessages.size().grow(0, 1),
                temporaryMessagePosition,
                temporaryMessagePosition
        );

        renderComponent(temporaryMessages, temporaryMessagesElement, "temporaryMessages");
    }

    private void renderComponent(ViewComponent component, ScreenElement element, String defaultKey) {
        ColorSetup defaultSetup = ColorMaster.instance().get(defaultKey);
        TextColor defaultForeground = toTextColor(defaultSetup.foreground(), Color.WHITE);
        TextColor defaultBackground = toTextColor(defaultSetup.background(), Color.BLACK);

        fillBackground(element, defaultBackground);

        for (int i = 0; i < component.lines().length; i++) {
            int screenRow = element.contentTopLeft().paragraph() + i;

            Line line = component.lines()[i];
            TextPosition lineStart = new TextPosition(screenRow, element.contentTopLeft().character());
            drawLine(line, lineStart, defaultForeground, defaultBackground);
        }
    }

    private void fillBackground(ScreenElement element, TextColor defaultBackground) {
        graphics.setBackgroundColor(defaultBackground);
        String string = " ".repeat(element.paddedSize().width());

        for (int i = 0; i < element.paddedSize().height(); i++) {
            int screenRow = i + element.elementTopLeft().paragraph();
            graphics.putString(element.elementTopLeft().character(), screenRow, string);
        }
    }

    private TextColor toTextColor(Color color, Color fallback) {
        if (color == null) {
            return toTextColor(fallback);
        }

        return toTextColor(color);
    }
    private TextColor toTextColor(Color color) {
        return new TextColor.RGB(color.getRed(), color.getGreen(), color.getBlue());
    }

    private void applyStyle(Style style) {
        graphics.clearModifiers();

        ColorSetup setup = ColorMaster.instance().get(style.renderStyle().toString());

        if (style.isBold() || setup.isBold()) {
            graphics.enableModifiers(SGR.BOLD);
        }
        if (style.isItalic() || setup.isItalic()) {
            graphics.enableModifiers(SGR.ITALIC);
        }

        if (setup.foreground() != null) {
            TextColor foregroundTextColor = toTextColor(setup.foreground());
            graphics.setForegroundColor(foregroundTextColor);
        }

        if (setup.background() != null) {
            TextColor backgroundTextColor = toTextColor(setup.background());
            graphics.setBackgroundColor(backgroundTextColor);
        }

    }

    private void drawLine(Line line, TextPosition lineStart, TextColor defaultForeground, TextColor defaultBackground) {
        int screenColumn = lineStart.character();

        for (TextSpan span : line.textSpans()) {
            graphics.setForegroundColor(defaultForeground);
            graphics.setBackgroundColor(defaultBackground);

            applyStyle(span.style());
            graphics.putString(screenColumn, lineStart.paragraph(), span.text());
            screenColumn += span.text().length();
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
