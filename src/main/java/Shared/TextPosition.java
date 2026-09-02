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

    public boolean equals(TextPosition position) {
        return paragraph == position.paragraph() && character == position.character();
    }

    public boolean equals(int paragraph, int character) {
        return paragraph == this.paragraph() && character == this.character();
    }

    public TextPosition offset(int paragraph, int character) {
        return new TextPosition(
            this.paragraph + paragraph,
            this.character + character
        );
    }
}
