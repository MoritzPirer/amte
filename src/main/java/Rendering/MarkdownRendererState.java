package Rendering;

import Controller.View.RenderStyle;
import Controller.View.Style;
import Shared.Utility.StringUtility;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MarkdownRendererState implements RendererState {
    private static final String ITALIC_INDICATOR = "*";
    private static final String BOLD_INDICATOR = "**";
    private static final String VERBATIM_INDICATOR = "`";
    private static final String ESCAPE_INDICATOR = "\\";

    private static final Set<String> delimiters = Set.of(ITALIC_INDICATOR, BOLD_INDICATOR, VERBATIM_INDICATOR);

    private static final String HEADING_INDICATOR = "#";
    private static final String QUOTE_INDICATOR = ">";

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
        Style baseStyle = baseStyle(tokenized);

        return tokenized.stream()
            .map(token -> parseToken(token, baseStyle))
            .toArray(StyleData[]::new);
    }

    private Style baseStyle(List<String> tokens) {
        if (isHeading(tokens)) {
            return Style.HEADING_STYLE;
        }

        if (isQuote(tokens)) {
            return Style.QUOTE_STYLE;
        }

        return Style.TEXT_STYLE;
    }

    private boolean isHeading(List<String> tokens) {
        if (tokens.isEmpty()) {
            return false;
        }

        Pattern pattern = Pattern.compile("^\\s*" + HEADING_INDICATOR + "+\\s+.*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(tokens.getFirst());

        return matcher.find();
    }

    private boolean isQuote(List<String> tokens) {
        return !tokens.isEmpty()
            && tokens.getFirst().startsWith(QUOTE_INDICATOR);
    }

    private StyleData parseToken(String token, Style baseStyle) {
        Style currentStyle = getCurrentStyle(baseStyle);

        if (token.equals(VERBATIM_INDICATOR)) {
            isVerbatim = !isVerbatim;
            return new StyleData(currentStyle, token.length());
        }

        if (isVerbatim) {
            return new StyleData(currentStyle, token.length());
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

            default -> { return new StyleData(currentStyle, token.length()); }
        }
    }

    private Style getCurrentStyle(Style baseStyle) {
        return new Style(
            isItalic || baseStyle.isItalic(),
            isBold || baseStyle.isBold(),
            baseStyle.renderStyle()
        );
    }

    @Override
    public void reset() {
        isBold = false;
        isItalic = false;
        isVerbatim = false;
    }
}
