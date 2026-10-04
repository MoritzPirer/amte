package FileStorage.FileIO;

import Debugging.DebugLogger;

import java.io.IOException;
import java.nio.file.*;

public class FileChangeMonitor {
    private Path path;
    private WatchService watcher;
    private boolean isActive;
    private boolean supressNextModification = false;

    public FileChangeMonitor(Path path) {
        track(path);
    }

    public void changePath(Path path) {
        track(path);
    }

    private boolean isValidPath(Path path) {
        return path != null && Files.exists(path) && !Files.isDirectory(path);
    }

    private void track(Path path) {
        if (!isValidPath(path)) {
            DebugLogger.warn("HOT_LOADING", "file path '" + path + "' is not trackable");
            isActive = false;
            return;
        }
        try {
            this.path = path.toAbsolutePath();
            this.watcher = FileSystems.getDefault().newWatchService();
            this.path.getParent().register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            this.isActive = true;
            DebugLogger.info("HOT_LOADING", "Set up WatchService for " + this.path);
        }
        catch (IOException e) {
            DebugLogger.warn("HOT_LOADING", "Failed to set up watch service for " + this.path);
            DebugLogger.info("HOT_LOADING", e.getMessage());
            this.isActive = false;
        }
    }

    /**
     * the next modification that is detected will be ignored. This prevents saves made by the editor from being
     * interpreted as external modifications
     */
    public void supressNextModification() {
        supressNextModification = true;
    }

    public boolean hasChanged() {
        if (!isActive) {
            return false;
        }

        WatchKey key = watcher.poll();
        if (key == null) {
            return false;
        }

        for (WatchEvent<?> event : key.pollEvents()) {
            if (event.kind() != StandardWatchEventKinds.ENTRY_MODIFY) {
                continue;
            }

            if (supressNextModification) {
                supressNextModification = false;
                continue;
            }

            if (event.context().equals(path.getFileName())) {
                DebugLogger.warn("HOT_LOADING", "change to registered file " + event.context());
                key.reset();
                return true;
            }

            DebugLogger.warn("HOT_LOADING", "Change was made to different file (" +  event.context() + ")");
        }

        key.reset();
        return false;
    }
}
