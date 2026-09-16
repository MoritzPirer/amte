package Rendering;

class RendererStateManager {
    private RendererState currentState;

    public RendererStateManager() {
        this.currentState = new MarkdownRendererState(this);
    }

    StyleData[] parse(String text) {
        return currentState.parse(text);
    }

    void reset() {
        currentState.reset();
    }

    void changeState(RendererState newState) {
        currentState = newState;
    }
}
