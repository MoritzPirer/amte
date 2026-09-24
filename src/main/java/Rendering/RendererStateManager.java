package Rendering;

import SpellChecking.SpellChecker;

class RendererStateManager {
    private RendererState currentState;

    public RendererStateManager() {
        this.currentState = new MarkdownRendererState(this);
    }

    StyleData[] parse(String text, SpellChecker spellChecker) {
        return currentState.parse(text, spellChecker);
    }

    /**
     * prepare the manager for a new rendering pass
     * @param fileExtension the file extension of the file, such that the appropriate rendering state can be set
     */
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
