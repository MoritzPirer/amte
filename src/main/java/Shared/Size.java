package Shared;

public record Size(int height, int width) {
    /**
     * creates a new Size Object that is heightChange units taller and widthChange units wider than this
     * @param heightChange how many units to grow vertically (negative to shrink)
     * @param widthChange how many units to grow horizontally (negative to shrink)
     * @return the grown Size
     */
    public Size grow(int heightChange, int widthChange) {
        return new Size(height + heightChange, width +  widthChange);
    }

}
