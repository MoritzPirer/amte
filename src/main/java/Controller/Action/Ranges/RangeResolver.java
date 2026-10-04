package Controller.Action.Ranges;

import Controller.ViewPortManager.ViewPort;
import FileStorage.TextFile;

public interface RangeResolver {
    /**
     * generate a Range based on the position in the file and the file contents
     * @param viewPort the viewport associated with the file
     * @param file the file that is being operated on
     * @return a Range within the file
     */
    Range resolve(ViewPort viewPort, TextFile file);
}
