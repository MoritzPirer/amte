package Rendering;

import Controller.View.Style;

class NullRendererState implements RendererState {

    @Override
    public StyleData[] parse(String paragraph) {
        return new StyleData[]{new StyleData(Style.TEXT_STYLE, paragraph.length())};
    }
}
