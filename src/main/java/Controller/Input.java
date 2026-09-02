package Controller;

import Shared.TextPosition;

public class Input {
    private final Character character;
    private final SpecialInput specialInput;
    private final TextPosition mousePosition;

    public Input(char character) {
        this.character = character;
        this.specialInput = null;
        this.mousePosition = null;
    }

    public Input(SpecialInput specialInput) {
        this.character = null;
        this.specialInput = specialInput;
        this.mousePosition = null;
    }
    public Input(TextPosition mousePosition) {
        this.character = null;
        this.specialInput = null;
        this.mousePosition = mousePosition;
    }

    public boolean isCharacter() {
        return character != null;
    }

    public char getCharacter() {
        if (character == null) {
            throw new RuntimeException("tried to access character when special input was given");
        }
        return character;
    }

    public boolean isSpecialInput() {
        return specialInput != null;
    }

    public SpecialInput getSpecialInput() {
        if (specialInput == null) {
            throw new RuntimeException("tried to access special input when character input was given");
        }

        return specialInput;
    }

    public boolean isMousePosition() {
        return mousePosition != null;
    }

    public TextPosition getMousePosition() {
        if (mousePosition == null) {
            throw new RuntimeException("tried to access mouse position when character input was given");
        }

        return mousePosition;
    }
}
