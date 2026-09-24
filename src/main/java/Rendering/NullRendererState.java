package Rendering;

import Controller.View.Style;
import SpellChecking.SpellChecker;

class NullRendererState implements RendererState {

    @Override
    public StyleData[] parse(String paragraph, SpellChecker spellChecker) {
        return new StyleData[]{new StyleData(Style.TEXT_STYLE, paragraph.length())};
    }
}
