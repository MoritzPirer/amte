package Rendering;

interface RendererState {
    StyleData[] parse(String paragraph);
    void reset();
}
