package Controller;

import Debugging.DebugLogger;

public class Input {
    private final Character character;
    private final SpecialInput specialInput;

    @Override
    public String toString() {
        if (specialInput != null) {
            return specialInput.toString();
        }
        assert character != null;
        return character.toString();
    }
    public Input(char character) {
        this.character = character;
        this.specialInput = null;
    }

    public Input(SpecialInput specialInput) {
        this.character = null;
        this.specialInput = specialInput;
    }

    public boolean isCharacter() {
        return character != null;
    }

    public char getCharacter() {
        if (character != null) {
            return character;
        }

        throw new RuntimeException("tried to access character when special input was given");
    }

    public boolean isSpecialInput() {
        return specialInput != null;
    }

    public SpecialInput getSpecialInput() {
        if (specialInput == null) {

            throw new RuntimeException("tried to access character when special input was given");
        }

        return specialInput;
    }
}
