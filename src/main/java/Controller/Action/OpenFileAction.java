package Controller.Action;

import Controller.TemporaryMessages.Message;
import Controller.TemporaryMessages.MessageType;
import Controller.TemporaryMessages.TimeDuration;
import Controller.ViewPortManager.ViewPort;
import Controller.ViewPortManager.ViewPortManager;
import FileStorage.*;
import FileStorage.FileIO.DiskFileLoader;
import LanguageSupport.LanguageProvider;
import Settings.SettingsMaster;
import SpellChecking.SpellChecker;
import SpellChecking.SpellCheckerLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class OpenFileAction extends Action {
    private final Path absolutePath;

    public OpenFileAction(Path absolutePath) {
        if (absolutePath == null || !Files.exists(absolutePath) || Files.isDirectory(absolutePath)) {
            throw new IllegalArgumentException("Constructed with an invalid path");
        }

        this.absolutePath = absolutePath;
    }

    @Override
    public void apply(ViewPortManager viewPortManager) {
        FileBuffer buffer;
        try {
            buffer = new DiskFileLoader().load(absolutePath);
        }
        catch (IOException e) {
            viewPortManager.current().addTemporaryMessage(new Message(
                new LanguageProvider().get("command/open/fail"),
                MessageType.ERROR,
                TimeDuration.seconds(5)
            ));
            return;
        }

        FileMetadataTracker tracker = new WrapperMetadataTracker(buffer);
        SpellChecker checker = SpellCheckerLoader.load(SettingsMaster.instance().getString("spellCheckLanguage"));
        String fileName = absolutePath.getFileName().toString();
        TextFile file = new TextFile(absolutePath, fileName, tracker, SaveState.SAVED, checker);

        ViewPort viewPort = new ViewPort(file);
        viewPortManager.add(viewPort);
    }
}
