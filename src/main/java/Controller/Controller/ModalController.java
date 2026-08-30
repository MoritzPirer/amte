package Controller.Controller;

import Controller.Action.Action;
import Controller.Modes.ModeManager;
import Controller.View.*;
import Controller.Input;
import Controller.ViewPort;
import Controller.SizeConfiguration;
import FileStorage.*;
import FileStorage.FileIO.DiskFileLoader;
import Settings.SettingsMaster;
import Shared.TextPosition;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModalController implements Controller{
    private TextFile file = null; //NOTE: will be used to access metadata like word- and character count
    private SizeConfiguration sizeConfiguration = null;
    private final ViewPort viewPort;
    private final ModeManager modeManager = new ModeManager();

    public ModalController() {
        FileBuffer buffer = new ListFileBuffer();
        FileMetadataTracker tracker = new WrapperMetadataTracker(buffer);

        //TODO WHEN [anytime] DO [pass working directory as filepath s.t. it can be saved OR make FileLoader default to wd if filepath is null]

        TextFile file = new TextFile(null, "hardcoded", tracker.getTrackedFileBuffer(), tracker, SaveState.CREATED);
        file.insert("Would thee like some hardcoded file contents, perchance?", new TextPosition(0, 0));
        file.insert("a SECOND paragraph? Daring today, are we?", new TextPosition(1, 0));
        file.insert("what if we - and bear with me here for just a brief moment - who hardcoded some file contents that are, quite frankly, on the long side, just to see the splitting logic working. You know, as a treat!", new TextPosition(2, 0));
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

        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        TextFile file = new TextFile(path, fileName, tracker.getTrackedFileBuffer(), tracker, SaveState.SAVED);
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
        action.apply(viewPort, file);
    }

    @Override
    public View getView() {
        ViewComponent textArea = viewPort.getTextArea(sizeConfiguration.textAreaSize());
        ViewComponent numberPanel = viewPort.getNumberPanel(sizeConfiguration.numberingSize());
        ViewComponent uiPanel = getUiPanel();
        TextPosition cursorPositionInText = viewPort.getVisualCursorPosition(sizeConfiguration.textAreaSize());

        return new View(textArea, uiPanel, numberPanel, cursorPositionInText);
    }

    private ViewComponent getUiPanel() {
        //TEMP
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
        return new TextSpan(Style.getModeIndicatorStyle(), modeManager.getName());
    }

    private TextSpan getFileName() {
        //TODO WHEN [Language Support is added] DO [make this function fetch strings from language provider]
        if (SettingsMaster.instance().getBoolean("showFileName")) {
            String fileName = file.getFileName();
            if (fileName == null || fileName.isEmpty()) {
                fileName = "untitled";
            }
            String text = "Now editing: " + fileName;
            return new TextSpan(Style.getFileNameStyle(), text);
        }
        return new TextSpan(Style.getFileNameStyle(), "");
    }

    private TextSpan getSaveStateIndicator() {
        //TODO WHEN [Language Support is added] DO [make this function fetch strings from language provider]
        String word = "";
        if (SettingsMaster.instance().getBoolean("showSaveStateWord")) {
           word = switch (file.getSaveState()) {
               case SAVED -> "saved";
               case MODIFIED -> "modified";
               case CREATED -> "created";
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

        String text = (icon.isEmpty() && word.isEmpty()) ? "" : icon + " " + word;
        return new TextSpan(Style.getSaveStateStyle(file.getSaveState()), text);
    }

    private TextSpan getCursorPositionIndicator() {
        if (SettingsMaster.instance().getBoolean("showCursorPosition")) {
            TextPosition position = viewPort.getLogicalCursorPosition();
            return new TextSpan(Style.getCursorPositionStyle(), position.toString());
        }

        return new TextSpan(Style.getCursorPositionStyle(), "");
    }


    private TextSpan[] getMetadataCounts() {
        List<TextSpan> textSpans = new ArrayList<>();

        if (SettingsMaster.instance().getBoolean("showCharacterCount")) {
            String characterCountText = "Character(s):" + String.valueOf(file.getCharacterCount());
            textSpans.add(new TextSpan(Style.getMetadataStyle(), characterCountText));
        }
        if (SettingsMaster.instance().getBoolean("showWordCount")) {
            String wordCountText = "Word(s):" + String.valueOf(file.getWordCount());
            textSpans.add(new TextSpan(Style.getMetadataStyle(), wordCountText));
        }
        if (SettingsMaster.instance().getBoolean("showParagraphCount")) {
            String paragraphCountText = "Paragraphs(s):" + String.valueOf(file.getParagraphCount());
            textSpans.add(new TextSpan(Style.getMetadataStyle(), paragraphCountText));
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
    public void updateSizeConfiguration(SizeConfiguration sizeConfiguration) {
        this.sizeConfiguration = sizeConfiguration;
    }
}
