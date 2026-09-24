package Controller.Controller;

import Controller.Action.Action;
import Controller.Action.NullAction;
import Controller.Modes.ModeManager;
import Controller.View.*;
import Controller.Input;
import Controller.ViewPort;
import Controller.SizeConfiguration;
import FileStorage.*;
import FileStorage.FileIO.DiskFileLoader;
import LanguageSupport.LanguageProvider;
import Settings.SettingsMaster;
import Shared.TextPosition;
import Shared.Utility.NumberUtility;
import SpellChecking.SpellChecker;
import SpellChecking.SpellCheckerLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModalController implements Controller{
    private final TextFile file;
    private SizeConfiguration sizeConfiguration = null;
    private final ViewPort viewPort;
    private final ModeManager modeManager = new ModeManager();
    private final LanguageProvider provider = new LanguageProvider();


    public ModalController() {
        FileBuffer buffer = new ListFileBuffer();
        FileMetadataTracker tracker = new WrapperMetadataTracker(buffer);

        //TODO WHEN [anytime] DO [pass working directory as filepath s.t. it can be saved OR make FileLoader default to wd if filepath is null]

        SpellChecker checker = SpellCheckerLoader.load(SettingsMaster.instance().getString("spellCheckLanguage"));
        TextFile file = new TextFile(null, "", tracker, SaveState.CREATED, checker);
        this.viewPort = new ViewPort(file);
        this.file = file;
    }

    public ModalController(String filePath) {
        FileBuffer buffer = new ListFileBuffer();
        try {
            new DiskFileLoader().load(filePath, buffer);
        }
        catch (IOException e) {
            buffer.clear();
            buffer.append("Error while loading file!");
            buffer.append(e.getMessage());
        }
        FileMetadataTracker tracker = new WrapperMetadataTracker(buffer);


        SpellChecker checker = SpellCheckerLoader.load(SettingsMaster.instance().getString("spellCheckLanguage"));

        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        TextFile file = new TextFile(path, fileName, tracker, SaveState.SAVED, checker);
        this.viewPort = new ViewPort(file);
        this.file = file;
    }

    @Override
    public boolean isRunning() {
        return viewPort.isRunning();
    }

    @Override
    public void applyInput(Input input) {
        Action action = modeManager.parseInput(input, viewPort, file);
        if (!(action instanceof NullAction)) {
            viewPort.clearTemporaryMessages();
        }

        action.apply(viewPort, file);
        viewPort.getHistory().add(action);
    }

    @Override
    public View getView() {
        ViewComponent textArea = viewPort.getTextArea(sizeConfiguration.textAreaSize());
        ViewComponent uiPanel = getUiPanel();
        ViewComponent numberPanel = viewPort.getNumberPanel(sizeConfiguration.numberingSize());
        ViewComponent temporaryMessagePanel = viewPort.getTemporaryMessages();
        TextPosition cursorPositionInText = viewPort.getVisualCursorPosition(sizeConfiguration.textAreaSize());

        return new View(textArea, uiPanel, numberPanel, temporaryMessagePanel, cursorPositionInText);
    }

    private ViewComponent getUiPanel() {
        List<TextSpan> spans = new ArrayList<>();
        spans.add(getModeIndicator());
        spans.add(getFileName());
        spans.add(getSaveStateIndicator());
        spans.add(getCursorPositionIndicator());
        spans.addAll(Arrays.asList(getMetadataCounts()));

        spans = addSeparator(spans);
        Line[] lines = reorganizeUiRows(spans);

        return new ViewComponent(sizeConfiguration.uiSize(), lines);
    }

    private Line[] reorganizeUiRows(List<TextSpan> spans) {
        final int maxWidth = sizeConfiguration.uiSize().width();
        int rowLength = 0;
        List<Line> lines = new ArrayList<>();
        List<TextSpan> currentLine = new ArrayList<>();

        for (TextSpan span : spans) {
            if (rowLength + span.text().length() > maxWidth) {
                lines.add(new Line(currentLine.toArray(new TextSpan[0])));
                currentLine.clear();
                rowLength = 0;
            }

            currentLine.add(span);
            rowLength += span.text().length();
        }

        if (!currentLine.isEmpty()) {
            lines.add(new Line(currentLine.toArray(new TextSpan[0])));
        }

        return lines.toArray(new Line[0]);
    }

    private TextSpan getModeIndicator() {
        return new TextSpan(Style.MODE_INDICATOR_STYLE, modeManager.getName());
    }

    private TextSpan getFileName() {
        if (SettingsMaster.instance().getBoolean("showFileName")) {
            String fileName = file.getFileName();
            if (fileName == null || fileName.isEmpty()) {
                fileName = provider.get("ui/file/untitled");
            }
            String text = provider.get("ui/file/nowEditing") + fileName;
            return new TextSpan(Style.FILE_NAME_STYLE, text);
        }
        return new TextSpan(Style.FILE_NAME_STYLE, "");
    }

    private TextSpan getSaveStateIndicator() {
        String word = "";
        if (SettingsMaster.instance().getBoolean("showSaveStateWord")) {
           word = switch (file.getSaveState()) {
               case SAVED -> provider.get("ui/saveState/saved");
               case MODIFIED -> provider.get("ui/saveState/modified");
               case CREATED -> provider.get("ui/saveState/created");
           };
        }

        String icon = "";
        if (SettingsMaster.instance().getBoolean("showSaveStateIcon")) {
            icon = switch (file.getSaveState()) {
                case SAVED -> "[=]";
                case MODIFIED -> "[+]";
                case CREATED -> "[!]";
            };
        }

        String text = (icon.isEmpty() && word.isEmpty()) ? "" : word + " " + icon;
        return new TextSpan(Style.getSaveStateStyle(file.getSaveState()), text);
    }

    private TextSpan getCursorPositionIndicator() {
        if (SettingsMaster.instance().getBoolean("showCursorPosition")) {
            TextPosition position = viewPort.getLogicalCursorPosition()
                .offset(1, 1); // convert from 0-indexed to 1-indexed

            return new TextSpan(Style.CURSOR_POSITION_STYLE, position.toString());
        }

        return new TextSpan(Style.CURSOR_POSITION_STYLE, "");
    }


    private TextSpan[] getMetadataCounts() {
        String separator = SettingsMaster.instance().getString("numberSeparator");
        List<TextSpan> textSpans = new ArrayList<>();

        if (SettingsMaster.instance().getBoolean("showCharacterCount")) {
            String characterCountText = provider.get("ui/counts/character")
                + NumberUtility.formatNumber(file.getCharacterCount(), separator);

            textSpans.add(new TextSpan(Style.METADATA_STYLE, characterCountText));
        }

        if (SettingsMaster.instance().getBoolean("showWordCount")) {
            String wordCountText = provider.get("ui/counts/word")
                + NumberUtility.formatNumber(file.getWordCount(), separator);

            textSpans.add(new TextSpan(Style.METADATA_STYLE, wordCountText));
        }

        if (SettingsMaster.instance().getBoolean("showParagraphCount")) {
            String paragraphCountText = provider.get("ui/counts/paragraph")
                + NumberUtility.formatNumber(file.getParagraphCount(), separator);

            textSpans.add(new TextSpan(Style.METADATA_STYLE, paragraphCountText));
        }

        if (textSpans.isEmpty()) {
            return new TextSpan[0];
        }

        return textSpans.toArray(new TextSpan[0]);
    }

    private List<TextSpan> addSeparator(List<TextSpan> textSpans) {
        final TextSpan seperator = new TextSpan(new Style(false, false, RenderStyle.TEXT), " | ");

        List<TextSpan> spans = new ArrayList<>();
        for (int i = 0; i < textSpans.size(); i++) {
            spans.add(textSpans.get(i));
            if (i < textSpans.size() - 1) {
                spans.add(seperator);
            }
        }

        return spans;

    }

    @Override
    public int getRecommendedNumberWidth() {
        return String.valueOf(file.length()).length();
    }

    @Override
    public void updateSizeConfiguration(SizeConfiguration sizeConfiguration) {
        this.sizeConfiguration = sizeConfiguration;
    }
}
