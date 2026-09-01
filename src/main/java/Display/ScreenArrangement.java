package Display;

import Controller.SizeConfiguration;

record ScreenArrangement(ScreenElement textArea, ScreenElement numberPanel, ScreenElement uiPanel) {
    public SizeConfiguration getSizeConfiguration() {
        return new SizeConfiguration(
            textArea.contentSize(),
            uiPanel.contentSize(),
            numberPanel.contentSize()
        );
    }

}
