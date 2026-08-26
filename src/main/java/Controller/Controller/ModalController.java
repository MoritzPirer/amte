package Controller.Controller;

import Controller.Action.Action;
import Controller.Modes.ModeManager;
import Controller.View.*;
import Controller.Input;
import Controller.ViewPort;
import Controller.SizeConfiguration;
import FileStorage.*;
import FileStorage.FileIO.DiskFileLoader;
import Shared.TextPosition;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModalController implements Controller{
    private TextFile file = null; //NOTE: will be used to access metadata like word- and character count
    private SizeConfiguration sizeConfiguration = null;
    private final ViewPort viewPort;
    private final ModeManager modeManager = new ModeManager();

    public ModalController() {
        FileBuffer buffer = new ListFileBuffer();
        FileMetadataTracker tracker = new WrapperMetadataTracker(buffer);

        TextFile file = new TextFile(null, "hardcoded", tracker.getTrackedFileBuffer(), tracker);
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
        TextFile file = new TextFile(path, fileName, tracker.getTrackedFileBuffer(), tracker);
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
        return new ViewComponent(sizeConfiguration.uiSize(), new Line[]{new Line(new TextSpan[]{new TextSpan(new Style(false, false, RenderStyle.TEXT), String.valueOf(viewPort.getLogicalCursorPosition()))})});
    }

    @Override
    public void updateSizeConfiguration(SizeConfiguration sizeConfiguration) {
        this.sizeConfiguration = sizeConfiguration;
    }
}
