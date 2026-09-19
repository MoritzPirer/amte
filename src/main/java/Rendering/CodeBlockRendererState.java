package Rendering;

import Controller.View.Style;
import Shared.Utility.NumberUtility;
import Shared.Utility.StringUtility;

import java.util.List;
import java.util.Objects;
import java.util.Set;

class CodeBlockRendererState implements RendererState{
    private static final String CODE_BLOCK_END = "```";

    private final RendererStateManager manager;

    private final CodeBlockInfo info;

    private boolean isWithinRangedComment;
    private boolean isWithinEndOfParagraphComment;
    private boolean isWithinStringLiteral;
    private String requiredClosingToken;

    public CodeBlockRendererState(String language, RendererStateManager manager) {

        this.info = CodeBlockInfoMaster.instance().getByLanguage(language);
        this.manager = manager;
    }

    public CodeBlockRendererState(CodeBlockInfo info, RendererStateManager manager) {
        this.info = info;
        this.manager = manager;
    }

    @Override
    public StyleData[] parse(String paragraph) {
        if (paragraph.endsWith("\n")) {
            paragraph = paragraph.substring(0, paragraph.length() - 1);
        }

        isWithinEndOfParagraphComment = false;

        if (isEndOfCodeBlock(paragraph)) {
            manager.changeState(new MarkdownRendererState(manager));
        }

        List<String> tokenized = StringUtility.tokenize(paragraph, info.delimiters(), null);

        return tokenized.stream()
            .map(this::parseToken)
            .toArray(StyleData[]::new);
    }

    private boolean isEndOfCodeBlock(String paragraph) {
        return paragraph.matches("^" + CODE_BLOCK_END + "\\s*$");
    }

    private StyleData parseToken(String token) {
        if (isWithinSpecialSection()) {
            return parseWithinSpecialSection(token);
        }

        // start of a string
        if (containsAsFirst(info.stringLiterals(), token)) {
            isWithinStringLiteral = true;
            requiredClosingToken = getClosing(info.stringLiterals(), token);
            return new StyleData(Style.CODE_STRING_LITERAL, token.length());
        }

        // start of a ranged comment
        if (containsAsFirst(info.rangedComments(), token)) {
            isWithinRangedComment = true;
            requiredClosingToken = getClosing(info.rangedComments(), token);
            return new StyleData(Style.CODE_COMMENT, token.length());
        }

        // start of an end-of-paragraph comment
        if (info.endOfParagraphComments().contains(token)) {
            isWithinEndOfParagraphComment = true;
            return new StyleData(Style.CODE_COMMENT, token.length());
        }

        // number literal
        if (NumberUtility.isNumber(token)) {
            return new StyleData(Style.CODE_NUMBER_LITERAL, token.length());
        }

        if (info.keywords().contains(token)) {
            return new StyleData(Style.CODE_KEYWORD, token.length());
        }

        if (info.builtins().contains(token)) {
            return new StyleData(Style.CODE_BUILTIN, token.length());
        }

        return new StyleData(Style.TEXT_STYLE, token.length());
    }

    /**
     * @return true if the token is part of a ranged styling section that can only be terminated by end of line or
     *         a closing token
     */
    private boolean isWithinSpecialSection() {
        return isWithinEndOfParagraphComment || isWithinStringLiteral || isWithinRangedComment;
    }

    /**
     * If currently in a ranged styling section, check if the token ends that section style it as part of that section
     * @param token the token to parse
     * @return the style of that token
     */
    private StyleData parseWithinSpecialSection(String token) {
        if (isWithinEndOfParagraphComment) {
            return new StyleData(Style.CODE_COMMENT, token.length());
        }

        if (isWithinStringLiteral) {
            if (token.equals(requiredClosingToken)) {
                requiredClosingToken = null;
                isWithinStringLiteral = false;
            }
            return new StyleData(Style.CODE_STRING_LITERAL, token.length());
        }

        assert isWithinRangedComment;

        if (token.equals(requiredClosingToken)) {
            requiredClosingToken = null;
            isWithinRangedComment = false;
        }
        return new StyleData(Style.CODE_COMMENT, token.length());
    }

    /**
     * checks if the given target is the first element of a StringPair in container
     * @param container the container to look through
     * @param target the target to look for
     * @return true if container has target as a first element, false otherwise
     */
    private boolean containsAsFirst(Set<StringPair> container, String target) {
        for (StringPair pair : container) {
            if (target.equals(pair.first())) {
                return true;
            }
        }

        return false;
    }

    /**
     * assuming that container has a pair that has opening as its first element, find the matching second element
     * @param container the container to look through
     * @param opening the opening token for which the closing counterpart should be found
     * @return the token that matches opening, or null if no such token exists
     */
    private String getClosing(Set<StringPair> container, String opening) {
        for (StringPair pair : container) {
            if (opening.equals(pair.first())) {
                return pair.second();
            }
        }

        return null;
    }
}
