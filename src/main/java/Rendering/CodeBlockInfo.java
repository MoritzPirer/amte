package Rendering;

import java.util.HashSet;
import java.util.Set;

record CodeBlockInfo(
    Set<String> keywords,
    Set<String> builtins,
    Set<String> endOfParagraphComments,
    Set<StringPair> rangedComments,
    Set<StringPair> stringLiterals,
    Set<String> delimiters
) {
    public static final CodeBlockInfo DUMMY = new CodeBlockInfo(
        new HashSet<>(),
        new HashSet<>(),
        new HashSet<>(),
        new HashSet<>(),
        new HashSet<>(),
        new HashSet<>()
    );
}
