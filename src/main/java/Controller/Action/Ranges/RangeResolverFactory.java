package Controller.Action.Ranges;

import Controller.ViewPort;
import FileStorage.TextFile;

public class RangeResolverFactory {
    public static ResolverState evaluate(char operator, String arguments) {
        if (!"dcy".contains(String.valueOf(operator))) {
            return ResolverState.INVALID;
        }

        char first = arguments.charAt(0);
        if (first == operator) {
            return ResolverState.VALID;
        }
        return switch (first) {
            case 'i', 'a' -> evaluateDelimiterChord(arguments);
            case 'w', 'W', 'b', 'B', 'e', 'E', 'h', 'j', 'k', 'l' -> ResolverState.VALID;
            case 't', 'T', 'f', 'F' -> {
                if (arguments.length() == 1) {
                    yield ResolverState.UNFINISHED;
                }
                yield ResolverState.VALID;
            }
            default -> ResolverState.INVALID;
        };

    }

    private static ResolverState evaluateDelimiterChord(String arguments) {
        if (arguments.length() < 2) {
            return ResolverState.UNFINISHED;
        }

        char second = arguments.charAt(1);

        if ("\"{[(<'wW".contains(String.valueOf(second))) {
            return ResolverState.VALID;
        }

        return ResolverState.INVALID;
    }

    public static RangeResolver create(char operator, String arguments, ViewPort viewPort, TextFile textFile) {
        /*
        assume operator d
        arguments:
            d -> current paragraph
            wWbBeE -> between current and cursor movement destination

            tTfF + _ -> to next occurrence
            ia + wW or delimiter
         */

        if (arguments.length() == 1 && operator == arguments.charAt(0)) {
            return new CurrentParagraphRangeResolver();
        }
        if (arguments.isEmpty() && Character.isUpperCase(operator)) {
            return new RestOfParagraphRangeResolver();
        }
        if (isFindChord(arguments)) {
            return createFindRange(operator, arguments, viewPort, textFile);
        }

        return null;
    }

    private static boolean isFindChord(String arguments) {
        if (arguments.length() != 2) {
            return false;
        }
        return "fFtT".contains(arguments.substring(0, 1));
    }

    private static RangeResolver createFindRange(char operator, String arguments, ViewPort viewPort, TextFile textFile) {
        assert arguments.length() == 2;

        char specifier = arguments.charAt(0);
        char target = arguments.charAt(1);
        boolean includeTarget = Character.toUpperCase(specifier) == 'F';
        boolean reversed = Character.isUpperCase(specifier);

        return new FindRangeResolver(target, includeTarget, reversed);
    }
}
