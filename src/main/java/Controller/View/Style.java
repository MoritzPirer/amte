package Controller.View;

public record Style(boolean isItalic, boolean isBold, RenderStyle renderStyle) {
    public static Style getNumberingStyle() {
        return new Style(true, false, RenderStyle.WEAK_HIGHLIGHT);
    }
}
