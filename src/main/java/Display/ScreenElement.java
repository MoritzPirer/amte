package Display;

import Shared.Size;
import Shared.TextPosition;

record ScreenElement(Size contentSize, Size paddedSize, TextPosition contentTopLeft, TextPosition elementTopLeft) {
    public ScreenElement(Size size, TextPosition topLeft) {
        this(size, size, topLeft, topLeft);
    }
}
