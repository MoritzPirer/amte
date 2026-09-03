package Shared;

public record Size(int height, int width) {
    public Size grow(int heightChange, int widthChange) {
        return new Size(height + heightChange, width +  widthChange);
    }

}
