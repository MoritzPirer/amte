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

    public void next() {
        currentIndex = (currentIndex + 1) % viewPorts.size();
    }
    public void previous() {
        currentIndex = (currentIndex - 1) % viewPorts.size();
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

    public void saveCurrent() {

    }

    public boolean isRunning() {
        return !viewPorts.isEmpty();
    }
}
