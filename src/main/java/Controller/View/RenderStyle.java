package Controller.View;

public enum RenderStyle {
    TEXT,

    WEAK_HIGHLIGHT,
    MEDIUM_HIGHLIGHT,
    STRONG_HIGHLIGHT,

    FILE_CREATED,
    FILE_MODIFIED,
    FILE_SAVED,

    CODE_BUILTIN,
    CODE_KEYWORD,
    CODE_STRING_LITERAL,
    CODE_NUMBER_LITERAL,
    CODE_COMMENT;

    public static String toString(RenderStyle style) {
        return switch (style) {
            case TEXT -> "text";
            case WEAK_HIGHLIGHT -> "weakHighlight";
            case MEDIUM_HIGHLIGHT -> "mediumHighlight";
            case STRONG_HIGHLIGHT -> "strongHighlight";
            case FILE_CREATED -> "fileCreated";
            case FILE_MODIFIED -> "fileModified";
            case FILE_SAVED -> "fileSaved";
            case CODE_BUILTIN -> "codeBuiltin";
            case CODE_KEYWORD -> "codeKeyword";
            case CODE_STRING_LITERAL -> "codeStringLiteral";
            case CODE_NUMBER_LITERAL -> "codeNumberLiteral";
            case CODE_COMMENT -> "codeComment";
        };
    }

    public String toString() {
        return toString(this);
    }
}