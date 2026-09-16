package Rendering;

import Controller.View.RenderStyle;
import Controller.View.Style;
import Shared.Utility.StringUtility;

import java.util.List;
import java.util.Set;

class MarkdownRendererState implements RendererState {
    private static final String ITALIC_INDICATOR = "*";
    private static final String BOLD_INDICATOR = "**";
    private static final String VERBATIM_INDICATOR = "`";
    private static final String ESCAPE_INDICATOR = "\\";

    private static final Set<String> delimiters = Set.of(ITALIC_INDICATOR, BOLD_INDICATOR, VERBATIM_INDICATOR);

    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean isVerbatim = false;

    private final RendererStateManager manager;

    public MarkdownRendererState(RendererStateManager manager) {
        this.manager = manager;
    }

    @Override
    public StyleData[] parse(String paragraph) {
        List<String> tokenized = StringUtility.tokenize(paragraph, delimiters, ESCAPE_INDICATOR);

        return  tokenized.stream()
            .map(this::parseToken)
            .toArray(StyleData[]::new);
    }


    private StyleData parseToken(String token) {
        if (token.equals(VERBATIM_INDICATOR)) {
            isVerbatim = !isVerbatim;
            return new StyleData(getCurrentStyle(), token.length());
        }

        if (isVerbatim) {
            return new StyleData(getCurrentStyle(), token.length());
        }

        switch (token) {
            case BOLD_INDICATOR -> {
                isBold = !isBold;
                return new StyleData(new Style(isItalic, true, RenderStyle.TEXT), token.length());
            }
            case ITALIC_INDICATOR -> {
                isItalic = !isItalic;
                return new StyleData(new Style(true, isBold, RenderStyle.TEXT), token.length());
            }

            default -> { return new StyleData(getCurrentStyle(), token.length()); }
        }
    }

    private Style getCurrentStyle() {
        return new Style(isItalic, isBold, RenderStyle.TEXT);
    }

    @Override
    public void reset() {
        isBold = false;
        isItalic = false;
        isVerbatim = false;
    }
}
