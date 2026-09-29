package Controller.Services.History;

import Controller.Action.Action;
import Controller.ViewPortManager.ViewPort;
import Controller.ViewPortManager.ViewPortManager;
import FileStorage.TextFile;

public interface History {
    void add(Action action);

    void undo(ViewPortManager viewPortManager);

    void redo(ViewPortManager viewPortManager);
}
