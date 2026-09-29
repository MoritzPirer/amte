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

public class FindAction extends Action {
    String target;
    boolean reversed;

    public FindAction(String target, boolean reversed) {
        assert target != null && !target.isEmpty();
        this.target = target;
        this.reversed = reversed;
    }

    @Override
    public void apply(ViewPortManager viewPortManager) {
        ViewPort viewPort = viewPortManager.current();

        TextIterator iterator = new TextIterator(viewPort, reversed);
        TextPosition match = reversed?
            knuthMorrisPratt(iterator, StringUtility.reverse(target)) :
            knuthMorrisPratt(iterator, target);

        if (match == null) {
            Message message = new Message(
                String.format(new LanguageProvider().get("command/find/notFound"), target),
                MessageType.ERROR,
                new InputCountDuration(1) //ANCHOR
            );
            viewPort.addTemporaryMessage(message);
        }
        else {
            viewPort.moveCursorTo(match);
        }
    }

    private TextPosition knuthMorrisPratt(TextIterator iterator, String target) {
        iterator.next(); //force move if cursor already on match
        int[] auxiliaryArray = precomputation(target);

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
                    positionInTarget = auxiliaryArray[positionInTarget];
                }

                startOfCurrentMatch = null;
            }

            current = iterator.current();
        }

        return null;
    }

    private static int[] precomputation(String target) {
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
        private final boolean reversed;

        TextIterator(ViewPort viewPort, boolean reversed) {
            this.file = viewPort.getFile();

            TextPosition cursor = viewPort.getLogicalCursorPosition();
            this.currentParagraph = file.readRawParagraph(cursor.paragraph());
            this.paragraphIndex = cursor.paragraph();
            this.positionInParagraph = cursor.character();

            this.currentCharacter = currentParagraph.charAt(positionInParagraph);
            this.reversed = reversed;
        }

        Character current() {
            return currentCharacter;
        }

        void next() {
            if (reversed) {
                backward();
            }
            else {
                forward();
            }
        }

        private void forward() {
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

        private void backward() {
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

        TextPosition position() {
            return new TextPosition(paragraphIndex, positionInParagraph);
        }
    }
}
