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
        CodeBlockInfo info = CodeBlockInfoMaster.instance().getCodeBlockInfo(language);
        this.info = Objects.requireNonNullElse(info, CodeBlockInfo.DUMMY);
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

        if (containsAsFirst(info.stringLiterals(), token)) {
            isWithinStringLiteral = true;
            requiredClosingToken = getClosing(info.stringLiterals(), token);
            return new StyleData(Style.CODE_STRING_LITERAL, token.length());
        }

        if (containsAsFirst(info.rangedComments(), token)) {
            isWithinRangedComment = true;
            requiredClosingToken = getClosing(info.rangedComments(), token);
            return new StyleData(Style.CODE_COMMENT, token.length());
        }

        if (info.endOfParagraphComments().contains(token)) {
            isWithinEndOfParagraphComment = true;
            return new StyleData(Style.CODE_COMMENT, token.length());
        }

        if (NumberUtility.isNumber(token)) {
            return new StyleData(Style.CODE_NUMBER_LITERAL, token.length());
        }

        if (info.builtins().contains(token)) {
            return new StyleData(Style.CODE_BUILTIN, token.length());
        }

        if (info.keywords().contains(token)) {
            return new StyleData(Style.CODE_KEYWORD, token.length());
        }

        return new StyleData(Style.TEXT_STYLE, token.length());
    }

    private boolean isWithinSpecialSection() {
        return isWithinEndOfParagraphComment || isWithinStringLiteral || isWithinRangedComment;
    }

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

    private boolean containsAsFirst(Set<StringPair> container, String target) {
        for (StringPair pair : container) {
            if (target.equals(pair.first())) {
                return true;
            }
        }

        return false;
    }

    private String getClosing(Set<StringPair> container, String opening) {
        for (StringPair pair : container) {
            if (opening.equals(pair.first())) {
                return pair.second();
            }
        }

        return null;
    }

    @Override
    public void reset() {
        isWithinEndOfParagraphComment = false;
        isWithinStringLiteral = false;
        isWithinRangedComment = false;
        requiredClosingToken = null;
    }
}
