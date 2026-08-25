package Controller;

public class Input {
    private final Character character;
    private final SpecialInput specialInput;

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
}
