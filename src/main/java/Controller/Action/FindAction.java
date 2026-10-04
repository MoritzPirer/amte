package Controller.Action;

import Controller.TemporaryMessages.InputCountDuration;
import Controller.TemporaryMessages.Message;
import Controller.TemporaryMessages.MessageType;
import Controller.ViewPortManager.ViewPort;
import Controller.ViewPortManager.ViewPortManager;
import FileStorage.TextFile;
import LanguageSupport.LanguageProvider;
import Shared.TextPosition;
import Shared.Utility.StringUtility;

/**
 * move the cursor to the next occurrence of a given target
 */
public class FindAction extends Action {
    String target;
    boolean doBackwardsSearch;

    public FindAction(String target, boolean doBackwardsSearch) {
        assert target != null && !target.isEmpty();
        this.target = target;
        this.doBackwardsSearch = doBackwardsSearch;
    }

    @Override
    public void apply(ViewPortManager viewPortManager) {
        ViewPort viewPort = viewPortManager.current();

        TextIterator iterator = new TextIterator(viewPort, doBackwardsSearch);
        TextPosition match = doBackwardsSearch ?
            findFirstMatch(iterator, StringUtility.reverse(target)) :
            findFirstMatch(iterator, target);

        if (match != null) {
            viewPort.moveCursorTo(match);
            return;
        }

        Message message = new Message(
            String.format(new LanguageProvider().get("command/find/notFound"), target),
            MessageType.ERROR,
            new InputCountDuration(1)
        );
        viewPort.addTemporaryMessage(message);
    }

    /**
     * search the string provided by iterator for the first occurrence of target. Implemented as a
     * variation of Knuth-Morris-Pratt String matching
     * @param iterator an iterator over the text to be searched
     * @param target the target to search for
     * @return the position where the first match starts, or null if no match was found
     */
    private TextPosition findFirstMatch(TextIterator iterator, String target) {
        iterator.next(); //force move if cursor already on match
        int[] skipAheadArray = calculateSkipAheadArray(target);

        int positionInTarget = 0;

        TextPosition startOfCurrentMatch = null;

        Character current = iterator.current();
        while (current != null) {
            if (current == target.charAt(positionInTarget)) {
                if (startOfCurrentMatch == null) {
                    startOfCurrentMatch = iterator.position();
                }

                if (positionInTarget == target.length() - 1) {
                    return startOfCurrentMatch;
                }

                positionInTarget++;
                iterator.next();
            }
            else {
                if (positionInTarget == 0) {
                    iterator.next();
                }
                else {
                    positionInTarget = skipAheadArray[positionInTarget];
                }

                startOfCurrentMatch = null;
            }

            current = iterator.current();
        }

        return null;
    }

    /**
     * calculates the auxiliary array for the Knuth-Morris-Pratt algorithm, indicating how far the search can skip
     * ahead on a mismatch, based on shared prefixes
     * @param target the search target
     * @return the auxiliary array
     */
    private static int[] calculateSkipAheadArray(String target) {
        int[] result = new int[target.length() + 1];

        result[0] = -1;
        result[1] = 0;

        for (int i = 2; i <= target.length(); i++) {
            int h = result[i - 1];
            while (h >= 0) {
                if (target.charAt(i - 1) == target.charAt(h)) {
                    result[i] = h + 1;
                    break;
                }

                h = result[h];
                if (h == -1) {
                    result[i] = 0;
                }
            }
        }

        return result;
    }

    /**
     * allows the string matching algorithm work the file content like a string rather than a 2D surface with row or column
     */
    private static class TextIterator {

        private final TextFile file;
        private String currentParagraph;
        private int paragraphIndex;
        private int positionInParagraph;
        private Character currentCharacter;
        private final boolean doBackwardsSearch;

        TextIterator(ViewPort viewPort, boolean doBackwardsSearch) {
            this.file = viewPort.getFile();

            TextPosition cursor = viewPort.getLogicalCursorPosition();
            this.currentParagraph = file.readRawParagraph(cursor.paragraph());
            this.paragraphIndex = cursor.paragraph();
            this.positionInParagraph = cursor.character();

            this.currentCharacter = currentParagraph.charAt(positionInParagraph);
            this.doBackwardsSearch = doBackwardsSearch;
        }

        Character current() {
            return currentCharacter;
        }

        /**
         * moves the iterator to the next character to be processed. In a backwards search, this is
         * the previous character of the Text being iterated over.
         */
        void next() {
            if (doBackwardsSearch) {
                moveBackward();
            }
            else {
                moveForward();
            }
        }

        private void moveForward() {
            positionInParagraph++;

            if (positionInParagraph < currentParagraph.length()) {
                currentCharacter = currentParagraph.charAt(positionInParagraph);
                return;
            }

            paragraphIndex++;
            if (paragraphIndex < file.length()) {
                currentParagraph = file.readRawParagraph(paragraphIndex);
                positionInParagraph = 0;

                currentCharacter = currentParagraph.charAt(positionInParagraph);
                return;
            }

            currentCharacter = null;
        }

        private void moveBackward() {
            positionInParagraph--;

            if (positionInParagraph >= 0) {
                currentCharacter = currentParagraph.charAt(positionInParagraph);
                return;
            }

            paragraphIndex--;
            if (paragraphIndex >= 0) {
                currentParagraph = file.readRawParagraph(paragraphIndex);
                positionInParagraph = currentParagraph.length() - 1;

                currentCharacter = currentParagraph.charAt(positionInParagraph);
                return;
            }

            currentCharacter = null;
        }

        /**
         * @return the position within the file the iterator is currently over
         */
        TextPosition position() {
            return new TextPosition(paragraphIndex, positionInParagraph);
        }
    }
}
