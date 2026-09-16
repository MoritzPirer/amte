package Rendering;

public interface Renderer {

    void start();
    void stop();

    StyleData[] getStyle(int paragraph);
}
