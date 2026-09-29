package Controller.Services.History;

import Controller.Action.Action;
import Controller.ViewPortManager.ViewPort;
import FileStorage.TextFile;

public interface History {
    void add(Action action);

    void undo(ViewPort viewPort, TextFile textFile);

    void redo(ViewPort viewPort, TextFile textFile);
}
