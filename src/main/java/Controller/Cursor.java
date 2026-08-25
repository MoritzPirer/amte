package Controller;

import FileStorage.ReadOnlyBufferView;
import Shared.Direction;
import Shared.TextPosition;


public class Cursor {
    private TextPosition position;
    private final ReadOnlyBufferView view;

    public Cursor(TextPosition position, ReadOnlyBufferView view) {
        this.position = position;
        this.view = view;
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
                Math.min(view.lengthOfParagraph(position.paragraph() - 1) - 1, position.character())
            );
        }
    }

    private void moveDown() {
        if (position.paragraph() == view.length() - 1) {
            position = new TextPosition(
                view.length() - 1,
                view.lengthOfParagraph(view.length() - 1) - 1
            );
        }
        else {
            position = new TextPosition(
                position.paragraph() + 1,
                Math.min(view.lengthOfParagraph(position.paragraph() + 1) - 1, position.character())
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
                view.lengthOfParagraph(position.paragraph() - 1) - 1
            );
        }
        else {
            position = new TextPosition(position.paragraph(), position.character() - 1);
        }
    }

    private void moveRight() {
        if (position.character() == view.lengthOfParagraph(position.paragraph()) - 1) {
            if (position.paragraph() == view.length() - 1) {
                return;
            }

            position = new TextPosition(position.paragraph() + 1, 0);
        }
        else {
            position = new TextPosition(position.paragraph(), position.character() + 1);
        }
    }
}
