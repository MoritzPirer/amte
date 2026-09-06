package Controller.Action.Ranges;

import Controller.ViewPort;
import FileStorage.TextFile;

public interface RangeResolver {
    Range resolve(ViewPort viewPort, TextFile file);
}
