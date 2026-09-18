package Rendering;

class RendererStateManager {
    private RendererState currentState;

    public RendererStateManager() {
        this.currentState = new MarkdownRendererState(this);
    }

    StyleData[] parse(String text) {
        return currentState.parse(text);
    }

    void reset(String fileExtension) {
        if (fileExtension.startsWith(".")) {
            fileExtension = fileExtension.substring(1);
        }

        switch (fileExtension) {
            case "md" -> currentState = new MarkdownRendererState(this);
            case "txt" -> currentState = new NullRendererState();
            default -> {
               CodeBlockInfo info = CodeBlockInfoMaster.instance().getByExtension(fileExtension);
               if (info == null) {
                   currentState = new NullRendererState();
               }
               else {
                   currentState = new CodeBlockRendererState(info, this);
               }
            }
        }
    }

    void changeState(RendererState newState) {
        currentState = newState;
    }
}
