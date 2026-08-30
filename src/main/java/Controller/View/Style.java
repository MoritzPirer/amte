package Controller.View;

import FileStorage.SaveState;

public record Style(boolean isItalic, boolean isBold, RenderStyle renderStyle) {
    public static Style getNumberingStyle() {
        return new Style(true, false, RenderStyle.WEAK_HIGHLIGHT);
    }
    public static Style getFileNameStyle() {
        return new Style(false, false, RenderStyle.WEAK_HIGHLIGHT);
    }

    public static Style getSaveStateStyle(SaveState saveState) {
        return new Style(
            false,
            false,
            switch (saveState) {
                case SAVED -> RenderStyle.FILE_SAVED;
                case MODIFIED -> RenderStyle.FILE_MODIFIED;
                case CREATED -> RenderStyle.FILE_CREATED;
            }
        );
    }

    public static Style getCursorPositionStyle() {
        return new Style(true, false, RenderStyle.TEXT);
    }

    public static Style getModeIndicatorStyle() {
        return new Style(false, false, RenderStyle.STRONG_HIGHLIGHT);
    }

    public static Style getMetadataStyle() {
        return new Style(false, false, RenderStyle.TEXT);
    }
}
