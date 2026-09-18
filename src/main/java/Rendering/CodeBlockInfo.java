package Rendering;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

record CodeBlockInfo(
    List<String> names,
    List<String> extensions,
    Set<String> keywords,
    Set<String> builtins,
    Set<String> endOfParagraphComments,
    Set<StringPair> rangedComments,
    Set<StringPair> stringLiterals,
    Set<String> delimiters,
    String numberSeparators
) {
    public static final CodeBlockInfo DUMMY = new CodeBlockInfo(
        new ArrayList<>(),
        new ArrayList<>(),
        new HashSet<>(),
        new HashSet<>(),
        new HashSet<>(),
        new HashSet<>(),
        new HashSet<>(),
        new HashSet<>(),
        ""
    );

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeBlockInfo other = (CodeBlockInfo) o;

        if (!this.names.equals(other.names)) return false;
        if (!this.extensions.equals(other.extensions)) return false;
        if (!this.keywords.equals(other.keywords)) return false;
        if (!this.builtins.equals(other.builtins)) return false;
        if (!this.endOfParagraphComments.equals(other.endOfParagraphComments)) return false;
        if (!this.rangedComments.equals(other.rangedComments)) return false;
        if (!this.stringLiterals.equals(other.stringLiterals)) return false;
        if (!this.delimiters.equals(other.delimiters)) return false;

        return this.numberSeparators.equals(other.numberSeparators);
    }

    @Override
    public int hashCode() {
        int result = names.hashCode();
        result = 31 * result + extensions.hashCode();
        result = 31 * result + keywords.hashCode();
        result = 31 * result + builtins.hashCode();
        result = 31 * result + endOfParagraphComments.hashCode();
        result = 31 * result + rangedComments.hashCode();
        result = 31 * result + stringLiterals.hashCode();
        result = 31 * result + delimiters.hashCode();
        result = 31 * result + numberSeparators.hashCode();

        return result;
    }
}
