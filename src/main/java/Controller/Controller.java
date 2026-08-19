package Controller;

import Controller.View.View;

public interface Controller {
    boolean isRunning();
    void applyInput(Input input);
    View getView();
    void updateSizeConfiguration(SizeConfiguration sizeConfiguration);
}
