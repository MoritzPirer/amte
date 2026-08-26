package Controller.View;

public record TextSpan(Style style, String text) {
    @Override
    public String toString() {
        return text;
    }
}
