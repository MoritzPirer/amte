package Controller;

import FileStorage.ReadableBuffer;
import Shared.Direction;
import Shared.TextPosition;


public class Cursor {
    private TextPosition position;
    private final ReadableBuffer buffer;

    public Cursor(TextPosition position, ReadableBuffer buffer) {
        this.position = position;
        this.buffer = buffer;
    }

    public TextPosition getPosition() {
        return position;
    }

    public void moveTo(TextPosition position) {
        this.position = position;
    }

    public void move(Direction direction) {
        switch (direction) {
            case UP -> moveUp();
            case DOWN -> moveDown();
            case LEFT -> moveLeft();
            case RIGHT -> moveRight();
        }
    }

    private void moveUp() {
        if (position.paragraph() == 0) {
            position = new TextPosition(0, 0);
        }
        else {
            position = new TextPosition(
                position.paragraph() - 1,
                Math.min(buffer.lengthOfParagraph(position.paragraph() - 1) - 1, position.character())
            );
        }
    }

    private void moveDown() {
        if (buffer.isEmpty()) {
            return;
        }
        if (position.paragraph() == buffer.length() - 1) {
            position = new TextPosition(
                buffer.length() - 1,
                buffer.lengthOfParagraph(buffer.length() - 1) - 1
            );
        }
        else {
            position = new TextPosition(
                position.paragraph() + 1,
                Math.min(buffer.lengthOfParagraph(position.paragraph() + 1) - 1, position.character())
            );
        }
    }

    private void moveLeft() {
        if (position.character() == 0) {
            if (position.paragraph() == 0)  {
                return;
            }
            position = new TextPosition(
                position.paragraph() - 1,
                buffer.lengthOfParagraph(position.paragraph() - 1) - 1
            );
        }
        else {
            position = new TextPosition(position.paragraph(), position.character() - 1);
        }
    }

    private void moveRight() {
        if (buffer.isEmpty()) {
            return;
        }

        if (position.character() == buffer.lengthOfParagraph(position.paragraph()) - 1) {
            if (position.paragraph() == buffer.length() - 1) {
                return;
            }

            position = new TextPosition(position.paragraph() + 1, 0);
        }
        else {
            position = new TextPosition(position.paragraph(), position.character() + 1);
        }
    }
}
