package Controller.Modes;

import Controller.Action.Action;
import Controller.Action.DeleteAction;
import Controller.Action.InsertAction;
import Debugging.DebugLogger;
import Shared.StringPair;
import Shared.TextPosition;
import Shared.Utility.StringUtility;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SmartList {
    private static final String CODE_BLOCK_FENCE = "```";

    //NOTE: 'STATIC' refers to the prefix being independent of the position within the list, as opposed to a dynamic
    // list, e.g. a numbered list like 1. 2. 3.
    private static final Set<StringPair> STATIC_LIST_AND_CONTINUATION = Set.of(
        new StringPair("- [ ] ", "- [ ] "),
        new StringPair("- [x] ", "- [ ] "),
        new StringPair("- ", "- "),
        new StringPair("+ ", "+ "),
        new StringPair("* ", "* "),
        new StringPair("> ", "> ")
    );

    private static final String DYNAMIC_LIST_INDICATORS = ".)";

    public static Action trySmartListInsertion(ParsingContext context) {
        DebugLogger.info("SMARTLIST", "Attempting a smart list insertion");
        TextPosition cursor = context.viewPort().getLogicalCursorPosition();

        if (!shouldTrySmartList(context)) {
            DebugLogger.info("SMARTLIST", "not eligible for smart list insertion based on file type and position in file");
            return basicParagraphSplit(cursor);
        }

        if (canDoStaticList(context)) {
            DebugLogger.info("SMARTLIST", "can do static smart list insertion");
            return makeStaticListInsertion(context);
        }

        if (canDoDynamicList(context)) {
            DebugLogger.info("SMARTLIST", "can do dynamic smart list insertion");
            return makeDynamicSmartListInsertion(context);
        }


        DebugLogger.info("SMARTLIST", "not eligible for smart list insertion based on content");
        return basicParagraphSplit(cursor);
    }

    private static boolean shouldTrySmartList(ParsingContext context) {
        String fileName = context.textFile().getFileName();
        String extension = StringUtility.fileNameToExtension(fileName);

        if (extension.equals("md") || extension.equals("txt")) {
            return !isWithinCodeBlock(context);
        }
        
        return false;
    }

    private static boolean isWithinCodeBlock(ParsingContext context) {
        boolean isWithinCodeBlock = false;

        // NOTE: this could probably be optimized, currently time complexity is linear w.r.t. length of file
        for (int index = 0; index < context.viewPort().getLogicalCursorPosition().paragraph(); index++) {
            String paragraph = context.textFile().readRawParagraph(index);

            if (isWithinCodeBlock && isEndOfCodeBlock(paragraph)) {
                isWithinCodeBlock = false;
            }
            else if (!isWithinCodeBlock && isStartOfCodeBlock(paragraph)) {
                isWithinCodeBlock = true;
            }
        }

        return isWithinCodeBlock;
    }

    private static boolean isStartOfCodeBlock(String paragraph) {
        return paragraph.startsWith(CODE_BLOCK_FENCE);
    }

    private static boolean isEndOfCodeBlock(String paragraph) {
        return paragraph.endsWith(CODE_BLOCK_FENCE)
            && StringUtility.containsOnlyIgnoringWhitespace(paragraph, CODE_BLOCK_FENCE);
    }

    private static Action basicParagraphSplit(TextPosition cursor) {
        return new InsertAction("\n",  cursor);
    }

    private static boolean canDoStaticList(ParsingContext context) {
        return findStaticListType(context) != null;
    }

    private static StringPair findStaticListType(ParsingContext context) {
        TextPosition cursor = context.viewPort().getLogicalCursorPosition();
        String paragraph = context.textFile().readRawParagraph(cursor.paragraph());
        String paragraphUntilCursor = paragraph.substring(0, cursor.character());

        for (StringPair pair : STATIC_LIST_AND_CONTINUATION) {
            if (StringUtility.startsWithIgnoringWhitespace(paragraphUntilCursor, pair.first())) {
                return pair;
            }
        }

        return null;
    }

    private static Action makeStaticListInsertion(ParsingContext context) {
        StringPair listType = findStaticListType(context);
        assert listType != null;

        TextPosition cursor = context.viewPort().getLogicalCursorPosition();
        String paragraph = context.textFile().readRawParagraph(cursor.paragraph());

        if (StringUtility.removeWhiteSpace(listType.first()).length() == StringUtility.removeWhiteSpace(paragraph).length()) {
            DebugLogger.info("SMARTLIST", "paragraph is only list prefix; exit list");
            return abortList(cursor, paragraph);
        }

        String paragraphUntilCursor = paragraph.substring(0, cursor.character());

        String leadingWhitespace = paragraph.substring(0, StringUtility.countLeadingWhitespace(paragraphUntilCursor));
        String continuation = leadingWhitespace + listType.second();

        return new InsertAction("\n" + continuation,  cursor);
    }

    private static Action abortList(TextPosition cursor, String paragraph) {
        return new DeleteAction(
            new TextPosition(cursor.paragraph(), 0),
            new TextPosition(cursor.paragraph(), paragraph.length() - 1)
        );
    }

    private static boolean canDoDynamicList(ParsingContext context) {
        TextPosition cursor = context.viewPort().getLogicalCursorPosition();
        String paragraph = context.textFile().readRawParagraph(cursor.paragraph());

        return canBeNumberList(paragraph) || canBeLetterList(paragraph);
    }

    private static boolean canBeNumberList(String paragraph) {
        if (paragraph.endsWith("\n")) {
            paragraph = paragraph.substring(0, paragraph.length() - 1);
        }

        Pattern pattern = Pattern.compile("^\\s*[0-9]+[" + DYNAMIC_LIST_INDICATORS + "]\\s+");
        Matcher matcher = pattern.matcher(paragraph);

        DebugLogger.warn("SMARTLIST", "canBeNumberList" + paragraph);
        return matcher.find();
    }

    private static boolean canBeLetterList(String paragraph) {
        Pattern pattern = Pattern.compile("^\\s*[a-zA-Z]+[" + DYNAMIC_LIST_INDICATORS + "]\\s+");
        Matcher matcher = pattern.matcher(paragraph);

        return matcher.find();
    }

    private static Action makeDynamicSmartListInsertion(ParsingContext context) {
        TextPosition cursor = context.viewPort().getLogicalCursorPosition();
        String paragraph = context.textFile().readRawParagraph(cursor.paragraph());

        if (canBeNumberList(paragraph)) {
            return makeNumberListInsertion(context);
        }

        return makeLetterListInsertion(context);
    }


    private static Action makeNumberListInsertion(ParsingContext context) {
        TextPosition cursor = context.viewPort().getLogicalCursorPosition();
        String paragraph = context.textFile().readRawParagraph(cursor.paragraph());

        int number = getNumberListValue(paragraph);
        Character listIndicator = StringUtility.findFirstOf(paragraph, DYNAMIC_LIST_INDICATORS);
        assert listIndicator != null;

        if (StringUtility.removeWhiteSpace(paragraph).equals(Integer.toString(number) + listIndicator)) {
            return abortList(cursor, paragraph);
        }

        String paragraphUntilCursor = paragraph.substring(0, cursor.character());
        String leadingWhitespace = paragraph.substring(0, StringUtility.countLeadingWhitespace(paragraphUntilCursor));
        String continuation = leadingWhitespace + (number + 1) + listIndicator + " ";

        return new InsertAction("\n" + continuation,  cursor);
    }

    private static int getNumberListValue(String paragraph) {
        String withoutWhitespace = paragraph.substring(StringUtility.countLeadingWhitespace(paragraph));
        String number = withoutWhitespace.substring(0, StringUtility.findFirstNonDigit(withoutWhitespace));

        return Integer.parseInt(number);
    }

    private static Action makeLetterListInsertion(ParsingContext context) {
        TextPosition cursor = context.viewPort().getLogicalCursorPosition();
        String paragraph = context.textFile().readRawParagraph(cursor.paragraph());

        char letter = getLetterListValue(paragraph);
        Character listIndicator = StringUtility.findFirstOf(paragraph, DYNAMIC_LIST_INDICATORS);
        assert listIndicator != null;

        if (StringUtility.removeWhiteSpace(paragraph).equals(String.valueOf(letter) + listIndicator)) {
            return abortList(cursor, paragraph);
        }

        String paragraphUntilCursor = paragraph.substring(0, cursor.character());
        String leadingWhitespace = paragraph.substring(0, StringUtility.countLeadingWhitespace(paragraphUntilCursor));
        String continuation = leadingWhitespace + (char)(letter + 1) + listIndicator + " ";

        return new InsertAction("\n" + continuation,  cursor);
    }

    private static char getLetterListValue(String paragraph) {
        String withoutWhitespace = paragraph.substring(StringUtility.countLeadingWhitespace(paragraph));

        assert !withoutWhitespace.isEmpty();

        return withoutWhitespace.charAt(0);
    }
}