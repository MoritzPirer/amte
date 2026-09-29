package Controller.ViewPortManager;

import java.util.ArrayList;
import java.util.List;

public class ViewPortManager {
    private final List<ViewPort> viewPorts = new ArrayList<>();
    private int currentIndex = 0;

    public ViewPortManager(ViewPort viewPort) {
        viewPorts.add(viewPort);
    }

    public ViewPort current() {
        return viewPorts.get(currentIndex);
    }

    public void add(ViewPort viewPort) {
        viewPorts.add(currentIndex + 1, viewPort);
    }

    public void next() {
        currentIndex++;
        normalizeIndex();
    }

    public void previous() {
        currentIndex--;
        normalizeIndex();
    }

    /**
     * ensure the index is in the range [0, number of viewports)
     */
    private void normalizeIndex() {
        currentIndex %= viewPorts.size();
        if (currentIndex < 0) {
            currentIndex += viewPorts.size();
        }
    }

    public int size() {
        return viewPorts.size();
    }

    public void closeCurrent() {
        viewPorts.get(currentIndex).stop();
        viewPorts.remove(currentIndex);

        if (currentIndex >= viewPorts.size()) {
            currentIndex = 0;
        }
    }

    public boolean isRunning() {
        return !viewPorts.isEmpty();
    }
}
