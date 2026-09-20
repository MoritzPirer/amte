package Controller.View;

import FileStorage.SaveState;

public record Style(boolean isItalic, boolean isBold, RenderStyle renderStyle) {
    public static final Style NUMBERING_STYLE = new Style(false, false, RenderStyle.TEXT);
    public static final Style FILE_NAME_STYLE = new Style(false, false, RenderStyle.WEAK_HIGHLIGHT);
    public static final Style CURSOR_POSITION_STYLE = new Style(true, false, RenderStyle.TEXT);

    public static Style getSaveStateStyle(SaveState saveState) {
        return new Style(false, false,
            switch (saveState) {
                case SAVED -> RenderStyle.FILE_SAVED;
                case MODIFIED -> RenderStyle.FILE_MODIFIED;
                case CREATED -> RenderStyle.FILE_CREATED;
            }
        );
    }

    public static final Style MODE_INDICATOR_STYLE = new Style(true, false, RenderStyle.STRONG_HIGHLIGHT);
    public static final Style METADATA_STYLE = new Style(false, false, RenderStyle.TEXT);
    public static final Style TEXT_STYLE = new Style(false, false, RenderStyle.TEXT);
    public static final Style HEADING_STYLE = new Style(false, false, RenderStyle.MEDIUM_HIGHLIGHT);
    public static final Style QUOTE_STYLE = new Style(false, false, RenderStyle.STRONG_HIGHLIGHT);

    public static final Style CODE_COMMENT = new Style(false, false, RenderStyle.CODE_COMMENT);
    public static final Style CODE_STRING_LITERAL = new Style(false, false, RenderStyle.CODE_STRING_LITERAL);
    public static final Style CODE_NUMBER_LITERAL = new Style(false, false, RenderStyle.CODE_NUMBER_LITERAL);
    public static final Style CODE_KEYWORD = new Style(false, false, RenderStyle.CODE_KEYWORD);
    public static final Style CODE_BUILTIN = new Style(false, false, RenderStyle.CODE_BUILTIN);

    public static final Style ERROR = new Style(false, false, RenderStyle.ERROR);

}
