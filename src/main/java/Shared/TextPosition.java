package Shared;

public record TextPosition(int paragraph, int character) {
    @Override
    public String toString() {
        //WARNING: exposed to the user through UI panel. do not change for debugging purposes
        return "(" + paragraph + ":" + character + ")";
    }

    public boolean isBefore(TextPosition position) {
        if (paragraph < position.paragraph()) {
            return true;
        }
        if (paragraph > position.paragraph()) {
            return false;
        }
        return character < position.character();
    }

    public boolean isAfter(TextPosition position) {
        if (paragraph > position.paragraph()) {
            return true;
        }
        if (paragraph < position.paragraph()) {
            return false;
        }
        return character > position.character();
    }

    public boolean isDirectlyBefore(TextPosition other) {
        if (this.paragraph != other.paragraph()) {
            return false;
        }

        return this.character + 1 == other.character;
    }

    public boolean equals(TextPosition position) {
        return paragraph == position.paragraph() && character == position.character();
    }

    public boolean equals(int paragraph, int character) {
        return paragraph == this.paragraph() && character == this.character();
    }

    /**
     * creates a new TextPosition that is offset from this by the given amount of paragraphs and columns.
     * It is not guaranteed that the resulting position is positive
     * @param paragraph how many paragraphs further down the new Position should be (negative value to go up)
     * @param character how many columns further right the new Position should be (negative value to go left)
     * @return the offset position
     */
    public TextPosition offset(int paragraph, int character) {
        return new TextPosition(
            this.paragraph + paragraph,
            this.character + character
        );
    }
}
