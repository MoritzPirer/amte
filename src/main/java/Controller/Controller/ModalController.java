package Controller.Controller;

import Controller.Action.Action;
import Controller.Action.NullAction;
import Controller.Modes.ModeManager;
import Controller.View.*;
import Controller.Input;
import Controller.ViewPortManager.ViewPort;
import Controller.SizeConfiguration;
import Controller.ViewPortManager.ViewPortManager;
import FileStorage.*;
import FileStorage.FileIO.DiskFileLoader;
import Settings.SettingsMaster;
import Shared.TextPosition;
import SpellChecking.SpellChecker;
import SpellChecking.SpellCheckerLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModalController implements Controller{
    private SizeConfiguration sizeConfiguration = null;
    private final ViewPortManager viewPortManager;
    private final ModeManager modeManager = new ModeManager();
    private final UiPanelAssembler uiPanelAssembler;


    public ModalController() {
        FileBuffer buffer = new ListFileBuffer();
        FileMetadataTracker tracker = new WrapperMetadataTracker(buffer);
        SpellChecker checker = SpellCheckerLoader.load(SettingsMaster.instance().getString("spellCheckLanguage"));

        TextFile file = new TextFile(null, "", tracker, SaveState.CREATED, checker);

        this.viewPortManager = new ViewPortManager(new ViewPort(file));
        this.uiPanelAssembler = new UiPanelAssembler(viewPortManager, modeManager);
    }

    public ModalController(String filePath) {
        FileBuffer buffer = new ListFileBuffer();
        Path path = Paths.get(filePath);
        try {
            new DiskFileLoader().load(path, buffer);
        }
        catch (IOException ignored) { }

        FileMetadataTracker tracker = new WrapperMetadataTracker(buffer);
        SpellChecker checker = SpellCheckerLoader.load(SettingsMaster.instance().getString("spellCheckLanguage"));

        String fileName = path.getFileName().toString();
        TextFile file = new TextFile(path, fileName, tracker, SaveState.SAVED, checker);

        this.viewPortManager = new ViewPortManager(new ViewPort(file));
        this.uiPanelAssembler = new UiPanelAssembler(viewPortManager, modeManager);
    }

    @Override
    public boolean isRunning() {
        return viewPortManager.isRunning();
    }

    @Override
    public void applyInput(Input input) {
        ViewPort viewPort = viewPortManager.current();
        Action action = modeManager.parseInput(input, viewPort, viewPort.getFile());

        if (!(action instanceof NullAction)) {
            viewPort.refreshTemporaryMessages();
        }

        action.apply(viewPortManager);
        viewPort.getHistory().add(action);

    }

    @Override
    public View getView() {
        ViewPort viewPort = viewPortManager.current();

        ViewComponent textArea = viewPort.getTextArea(sizeConfiguration.textAreaSize());
        ViewComponent uiPanel = uiPanelAssembler.getUiPanel(sizeConfiguration.uiSize());
        ViewComponent numberPanel = viewPort.getNumberPanel(sizeConfiguration.numberingSize());
        ViewComponent temporaryMessagePanel = viewPort.getTemporaryMessages();
        TextPosition cursorPositionInText = viewPort.getVisualCursorPosition(sizeConfiguration.textAreaSize());

        return new View(textArea, uiPanel, numberPanel, temporaryMessagePanel, cursorPositionInText);
    }

    @Override
    public int getRecommendedNumberWidth() {
        return String.valueOf(viewPortManager.current().getFile().length()).length();
    }

    @Override
    public void updateSizeConfiguration(SizeConfiguration sizeConfiguration) {
        this.sizeConfiguration = sizeConfiguration;
    }

    @Override
    public void update() {
        viewPortManager.current().tickTemporaryMessages();
        viewPortManager.current().handleFileChange();
    }
}
