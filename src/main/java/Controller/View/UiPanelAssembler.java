package Controller.View;

import Controller.Modes.ModeManager;
import Controller.ViewPortManager.ViewPortManager;
import FileStorage.SaveState;
import FileStorage.TextFile;
import LanguageSupport.LanguageProvider;
import Settings.SettingsMaster;
import Shared.Size;
import Shared.TextPosition;
import Shared.Utility.NumberUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UiPanelAssembler {
    private final LanguageProvider provider = new LanguageProvider();
    private final ViewPortManager viewPortManager;
    private final ModeManager modeManager;

    public UiPanelAssembler(ViewPortManager viewPortManager, ModeManager modeManager) {
        this.viewPortManager = viewPortManager;
        this.modeManager = modeManager;
    }

    public ViewComponent getUiPanel(Size uiSize) {
        List<TextSpan> spans = new ArrayList<>();
        spans.add(getModeIndicator());
        spans.add(getFileName());
        spans.add(getSaveStateIndicator());
        spans.add(getCursorPositionIndicator());
        spans.addAll(Arrays.asList(getMetadataCounts()));

        spans = addSeparator(spans);
        Line[] lines = reorganizeUiRows(spans, uiSize);

        return new ViewComponent(uiSize, lines);
    }

    private Line[] reorganizeUiRows(List<TextSpan> spans, Size uiSize) {
        final int maxWidth = uiSize.width();
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
            TextFile file = viewPortManager.current().getFile();
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
        SaveState saveState = viewPortManager
                .current()
                .getFile()
                .getSaveState();

        String word = "";
        if (SettingsMaster.instance().getBoolean("showSaveStateWord")) {
            word = switch (saveState) {
                case SAVED -> provider.get("ui/saveState/saved");
                case MODIFIED -> provider.get("ui/saveState/modified");
                case CREATED -> provider.get("ui/saveState/created");
            };
        }

        String icon = "";
        if (SettingsMaster.instance().getBoolean("showSaveStateIcon")) {
            icon = switch (saveState) {
                case SAVED -> "[=]";
                case MODIFIED -> "[+]";
                case CREATED -> "[!]";
            };
        }

        String text = (icon.isEmpty() && word.isEmpty()) ? "" : word + " " + icon;
        return new TextSpan(Style.getSaveStateStyle(saveState), text);
    }

    private TextSpan getCursorPositionIndicator() {
        if (SettingsMaster.instance().getBoolean("showCursorPosition")) {
            TextPosition position = viewPortManager.current().getLogicalCursorPosition()
                    .offset(1, 1); // convert from 0-indexed to 1-indexed

            return new TextSpan(Style.CURSOR_POSITION_STYLE, position.toString());
        }

        return new TextSpan(Style.CURSOR_POSITION_STYLE, "");
    }


    private TextSpan[] getMetadataCounts() {
        TextFile file = viewPortManager.current().getFile();
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
}
