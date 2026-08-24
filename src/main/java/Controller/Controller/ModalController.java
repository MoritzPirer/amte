package Controller.Controller;

import Controller.Action.Action;
import Controller.Modes.ModeManager;
import Controller.View.*;
import Controller.Input;
import Controller.ViewPort;
import Controller.SizeConfiguration;
import FileStorage.*;
import Shared.TextPosition;

public class ModalController implements Controller{
    private TextFile file = null; //NOTE: will be used to access metadata like word- and character count
    private SizeConfiguration sizeConfiguration = null;
    private final ViewPort viewPort;
    private final ModeManager modeManager = new ModeManager();

    public ModalController() {
        FileBuffer buffer = new ListFileBuffer();
        FileMetadataTracker tracker = new WrapperMetadataTracker(buffer);

        TextFile file = new TextFile("", "hardcoded", tracker.getTrackedFileBuffer(), tracker);
        file.insert("Would thee like some hardcoded file contents, perchance?", new TextPosition(0, 0));
        file.insert("a SECOND paragraph? Daring today, are we?", new TextPosition(1, 0));
        file.insert("what if we - and bear with me here for just a brief moment - who hardcoded some file contents that are, quite frankly, on the long side, just to see the splitting logic working. You know, as a treat!", new TextPosition(2, 0));
        this.viewPort = new ViewPort(file);
        this.file = file;
    }

    @Override
    public boolean isRunning() {
        return viewPort.isRunning();
    }

    @Override
    public void applyInput(Input input) {
        Action action = modeManager.parseInput(input, viewPort);
        action.apply(viewPort);
    }

    @Override
    public View getView() {
        ViewComponent textArea = viewPort.getTextArea(sizeConfiguration.textAreaSize());
        ViewComponent numberPanel = viewPort.getNumberPanel(sizeConfiguration.numberingSize());
        ViewComponent uiPanel = getUiPanel();
        TextPosition visualCursorPosition = viewPort.getVisualCursorPosition(sizeConfiguration.textAreaSize());
        //TODO WHEN [anytime] DO [add cursor position to View class, retrieve screen position from Viewport]
        return new View(textArea, uiPanel, numberPanel, visualCursorPosition);
    }

    private ViewComponent getUiPanel() {
        //TEMP
        return new ViewComponent(sizeConfiguration.uiSize(), new Line[]{new Line(new TextSpan[]{new TextSpan(new Style(false, false, RenderStyle.TEXT), modeManager.getName())})});
    }

    @Override
    public void updateSizeConfiguration(SizeConfiguration sizeConfiguration) {
        this.sizeConfiguration = sizeConfiguration;
    }
}
