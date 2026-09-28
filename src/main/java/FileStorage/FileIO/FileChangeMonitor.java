package FileStorage.FileIO;

import Debugging.DebugLogger;

import java.io.IOException;
import java.nio.file.*;

public class FileChangeMonitor {
    private Path path;
    private WatchService watcher;
    private boolean isActive;

    public FileChangeMonitor(Path path) {
        assert Files.exists(path) && !Files.isDirectory(path);

        this.isActive = true;
        track(path);
    }

    public void changePath(Path path) {
        assert Files.exists(path) && !Files.isDirectory(path);
        track(path);
    }

    private void track(Path path) {
        try {
            this.path = path.toAbsolutePath();
            this.watcher = FileSystems.getDefault().newWatchService();
            this.path.getParent().register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);
            this.isActive = true;
            DebugLogger.info("HOT_LOADING", "Set up WatchService");
        }
        catch (IOException e) {
            DebugLogger.warn("HOT_LOADING", "Failed to set up watch service for " + this.path);
            DebugLogger.info("HOT_LOADING", e.getMessage());
            this.isActive = false;
        }
    }

    public boolean hasChanged() {
        if (!isActive) {
            DebugLogger.warn("HOT_LOADING", "Not checking for change because setup failed");
            return false;
        }

        WatchKey key = watcher.poll();
        if (key == null) {
            return false;
        }

        for (WatchEvent<?> event : key.pollEvents()) {
            assert event.kind() == StandardWatchEventKinds.ENTRY_MODIFY;

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
