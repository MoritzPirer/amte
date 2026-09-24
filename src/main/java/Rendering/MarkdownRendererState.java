package Rendering;

import Controller.View.RenderStyle;
import Controller.View.Style;
import Settings.SettingsMaster;
import Shared.Utility.StringUtility;
import SpellChecking.SpellChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class MarkdownRendererState implements RendererState {
    private static final String ITALIC_INDICATOR = "*";
    private static final String BOLD_INDICATOR = "**";
    private static final String VERBATIM_INDICATOR = "`";
    private static final String ESCAPE_INDICATOR = "\\";
    private static final String CODE_BLOCK_INDICATOR = "```";
    private static final String HEADING_INDICATOR = "#";
    private static final String QUOTE_INDICATOR = ">";

    private static final Set<String> delimiters = Set.of(
        ITALIC_INDICATOR,
        BOLD_INDICATOR,
        VERBATIM_INDICATOR,
        CODE_BLOCK_INDICATOR
    );

    private boolean doSpellCheck;
    private boolean isBold = false;
    private boolean isItalic = false;
    private boolean isVerbatim = false;

    private final RendererStateManager manager;

    public MarkdownRendererState(RendererStateManager manager) {
        this.manager = manager;
        this.doSpellCheck = SettingsMaster.instance().getBoolean("doSpellCheck");
    }

    @Override
    public StyleData[] parse(String paragraph, SpellChecker spellChecker) {
        if (paragraph.endsWith("\n")) {
            paragraph = paragraph.substring(0, paragraph.length() - 1);
        }

        isVerbatim = false; //verbatim resets with end of paragraph, bold and italic continue

        List<String> tokenized = StringUtility.tokenize(paragraph, delimiters, ESCAPE_INDICATOR);
        Style baseStyle = baseStyle(tokenized);

        if (forceBaseStyle(tokenized)) {
            return new StyleData[]{new StyleData(baseStyle, paragraph.length())};
        }

        List<StyleData> styles = new ArrayList<>();
        for (String token : tokenized) {
            styles.addAll(parseToken(token, baseStyle, spellChecker));
        }
        return styles.toArray(StyleData[]::new);
    }

    /**
     * checks if the paragraph should have a default style other than the one for regular text
     * @param tokens the tokenized paragraph
     * @return what Style should be used if no other is required
     */
    private Style baseStyle(List<String> tokens) {
        if (isHeading(tokens)) {
            return Style.HEADING_STYLE;
        }

        if (isQuote(tokens)) {
            return Style.QUOTE_STYLE;
        }

        if (isCodeBlockStart(tokens)) {
            String requestedLanguage = getCodeBlockLanguage(tokens);
            manager.changeState(new CodeBlockRendererState(requestedLanguage, manager));
        }

        return Style.TEXT_STYLE;
    }

    private boolean isHeading(List<String> tokens) {
        if (tokens.isEmpty()) {
            return false;
        }

        //TODO
        // WHEN [anytime]
        // DO [introduce 6 heading limit]
        Pattern pattern = Pattern.compile("^\\s*" + HEADING_INDICATOR + "+\\s+.*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(tokens.getFirst());

        return matcher.find();
    }

    private boolean isQuote(List<String> tokens) {
        return !tokens.isEmpty()
            && tokens.getFirst().startsWith(QUOTE_INDICATOR);
    }

    private boolean isCodeBlockStart(List<String> tokens) {
        return !tokens.isEmpty() && tokens.getFirst().equals(CODE_BLOCK_INDICATOR);
    }

    /**
     * decide if the base style should be used for all tokens, or if every token should be parsed.
     * Currently necessary to prevent spellchecking from flagging code language indicator in code block starts
     * @return true if the base style should be used for all tokens, false if every token should be parsed
     */
    private boolean forceBaseStyle(List<String> tokens) {
        return isCodeBlockStart(tokens);
    }
    /**
     * retrieves the language specified by the start of a code block. Assumes that the paragraph contains a valid start
     * to a code block as specified by isCodeBlockStart()
     * @param tokens the paragraph that is the start of a code block.
     * @return the specified code language, or an empty string if none was specified
     */
    private String getCodeBlockLanguage(List<String> tokens) {
        if (tokens.size() < 2) {
            return "";
        }

        return StringUtility.removeWhiteSpace(tokens.get(1));
    }

    private List<StyleData> parseToken(String token, Style baseStyle, SpellChecker spellChecker) {
        Style currentStyle = getCurrentStyle(baseStyle);

        if (token.equals(VERBATIM_INDICATOR)) {
            isVerbatim = !isVerbatim;
            return List.of(new StyleData(currentStyle, token.length()));
        }

        // never change bold or italic in a verbatim section
        if (isVerbatim) {
            return List.of(new StyleData(currentStyle, token.length()));
        }

        switch (token) {
            case BOLD_INDICATOR -> {
                isBold = !isBold;
                return List.of(new StyleData(new Style(isItalic, true, RenderStyle.TEXT), token.length()));
            }
            case ITALIC_INDICATOR -> {
                isItalic = !isItalic;
                return List.of(new StyleData(new Style(true, isBold, RenderStyle.TEXT), token.length()));
            }
            default -> {
                if (doSpellCheck) {
                    return applySpellCheck(token, currentStyle, spellChecker);
                }

                return List.of(new StyleData(currentStyle, token.length()));
            }
        }
    }

    /**
     * constructs the current style by combining toggleable settings with the base style
     * @param baseStyle the base style on which italic and bold request are layered
     * @return the style to use
     */
    private Style getCurrentStyle(Style baseStyle) {
        return new Style(
            isItalic || baseStyle.isItalic(),
            isBold || baseStyle.isBold(),
            baseStyle.renderStyle()
        );
    }

    private List<StyleData> applySpellCheck(String token, Style currentStyle, SpellChecker spellChecker) {
        if (SpellChecker.shouldSplit(token)) {
            String[] tokens = SpellChecker.split(token);
            List<StyleData> styles = new ArrayList<>();

            for (String t : tokens) {
                styles.addAll(applySpellCheck(t, currentStyle, spellChecker));
            }

            return styles;
        }

        if (!SpellChecker.canBeSpellChecked(token)) {
            return List.of(new StyleData(currentStyle, token.length()));
        }

        if (spellChecker.check(token)) {
            return List.of(new StyleData(currentStyle, token.length()));
        }

        return List.of(new StyleData(getCurrentStyle(Style.ERROR), token.length()));
    }
}