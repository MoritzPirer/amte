package FileStorage;

import Controller.Action.Ranges.Range;
import Controller.TemporaryMessages.Message;
import Controller.TemporaryMessages.MessageType;
import Controller.TemporaryMessages.TimeDuration;
import Controller.View.Line;
import Debugging.DebugLogger;
import FileStorage.FileIO.DiskFileLoader;
import FileStorage.FileIO.FileChangeMonitor;
import FileStorage.FileIO.FileLoader;
import LanguageSupport.LanguageProvider;
import Rendering.DoubleBufferRenderer;
import Rendering.Renderer;
import Settings.SettingsMaster;
import Shared.TextPosition;
import Shared.Utility.StringUtility;
import SpellChecking.SpellChecker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class TextFile {
    private Path filePath;
    private String fileName;

    private FileBuffer buffer;
    private FileMetadataTracker tracker;

    private SaveState saveState;

    private final SpellChecker spellChecker;
    private final Renderer renderer;
    private final FileChangeMonitor fileChangeMonitor;

    public TextFile(Path filePath, String fileName, FileMetadataTracker tracker, SaveState saveState, SpellChecker spellChecker) {
        this.filePath = filePath;
        this.fileName = fileName;
        this.tracker = tracker;
        this.buffer = tracker.getTrackedFileBuffer();
        this.saveState = saveState;
        this.spellChecker = spellChecker;
        this.fileChangeMonitor = new FileChangeMonitor(filePath);

        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        renderer = new DoubleBufferRenderer(buffer, extension, spellChecker);
        renderer.start();
    }

    public void stop() {
        renderer.stop();
    }

    public void setFileName(String fileName) {
        DebugLogger.info("TEXTFILE", "renaming file to " + fileName);

        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        this.fileName = fileName;
        filePath = constructFilePath(fileName);
        fileChangeMonitor.changePath(filePath);

        String extension = StringUtility.fileNameToExtension(fileName);
        renderer.registerFileTypeChange(extension);
    }

    private Path constructFilePath(String fileName) {
        Path folder = filePath == null ? Path.of(System.getProperty("user.dir")) : filePath.getParent();
        return folder.resolve(fileName).normalize();
    }

    public String getFileName() {
        return fileName;
    }

    public Message handleFileChange() {
        if (!fileChangeMonitor.hasChanged()) {
            return null;
        }

        LanguageProvider provider = new LanguageProvider();
        if (saveState == SaveState.SAVED) {
            reloadBuffer();
            return new Message(
                provider.get("file/hotLoadApplied"),
                MessageType.INFO,
                TimeDuration.seconds(5)
            );
        }

        return new Message(
            provider.get("file/hotLoadSuppressed"),
            MessageType.ERROR,
            TimeDuration.seconds(5)
        );
    }

    private void reloadBuffer() {
        try {
            FileBuffer temp = new DiskFileLoader().load(filePath);
            buffer = temp;
            tracker = new WrapperMetadataTracker(temp);
        }
        catch (IOException ignored) { }
    }

    public void save() {
        filePath = Path.of(filePath.getParent() + File.separator + fileName);

        FileLoader loader = new DiskFileLoader();
        try {
            fileChangeMonitor.supressNextModification();
            loader.save(filePath, buffer);
            saveState = SaveState.SAVED;
        } catch (IOException ignored) { /* user will be informed that saving failed based on saveState */ }
    }

    public boolean isSaved() {
        return saveState == SaveState.SAVED;
    }

    public SaveState getSaveState() {
        return saveState;
    }

    public FileBuffer getBuffer() {
        return buffer;
    }

    public void insert(String content, TextPosition position) {
        buffer.insert(content, position);
        markAsModified();
    }

    private void markAsModified() {
        if (saveState != SaveState.CREATED) {
            saveState = SaveState.MODIFIED;
        }
        renderer.requestRefresh();
    }

    public Line readParagraph(int paragraphIndex) {
        return Line.of(buffer.readParagraph(paragraphIndex), renderer.getStyle(paragraphIndex));
    }

    public String readRawParagraph(int index) {
        return buffer.readParagraph(index);
    }

    public String readRange(TextPosition start, TextPosition end) {
        //TODO WHEN [anytime] DO [rewrite FileBuffer interface (and corresponding tests) to return String, not List<String>]
        List<String> list = buffer.readRange(start, end);
        StringBuilder builder = new StringBuilder();
        for (String string : list) {
            builder.append(string);
        }

        return builder.toString();
    }

    public void deleteRange(TextPosition start, TextPosition end) {
        buffer.delete(start, end);
        markAsModified();
    }

    public int length() {
        return buffer.length();
    }

    public int lengthOfParagraph(int paragraphIndex) {
        return buffer.lengthOfParagraph(paragraphIndex);
    }

    public int getWordCount() {
        //TODO WHEN [anytime] DO [change tracker interface to return int instead of long]
        return (int) tracker.getWordCount();
    }

    public int getCharacterCount() {
        return (int) tracker.getCharacterCount();
    }

    public int getParagraphCount() {
        return (int) tracker.getParagraphCount();
    }

    public TextPosition nextPosition(TextPosition position) {
        if (buffer.isEmpty()) {
            return position.offset(0, 1);
        }
        if (position.paragraph() < buffer.length() && position.character() >= buffer.lengthOfParagraph(position.paragraph()) - 1) {
            return new TextPosition(position.paragraph() + 1, 0);
        }

        return position.offset(0, 1);
    }

    public TextPosition previousPosition(TextPosition position) {
        if (position.equals(0, 0)) {
            return null;
        }
        if (position.character() == 0) {
            return new TextPosition(position.paragraph() - 1, buffer.lengthOfParagraph(position.paragraph() - 1) - 1);
        }

        return new TextPosition(position.paragraph(), position.character() - 1);
    }

    public boolean positionExists(TextPosition position) {
        if (position.paragraph() < 0 || position.paragraph() >= buffer.length()) {
            return false;
        }

        return position.character() >= 0 && position.character() < buffer.lengthOfParagraph(position.paragraph());
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    public String[] generateCorrections(Range range) {
        String word = buffer.readRange(range.start(), range.end()).getFirst();
        int maxResults = SettingsMaster.instance().getInt("maxCorrections");

        DebugLogger.info("SPELLCHECK", "searching for up to " + maxResults + " corrections for '" + word + "'");

        return spellChecker.correct(word, maxResults);
    }

    public boolean addToDictionary(String word) {
        boolean result = spellChecker.add(word);
        renderer.requestRefresh();

        return result;
    }
}
