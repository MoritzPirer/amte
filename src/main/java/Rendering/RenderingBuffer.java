package Rendering;

import java.util.ArrayList;
import java.util.List;

class RenderingBuffer {
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        for (StyleData[] line : buffer) {
            builder.append(i);
            builder.append(": ");
            for (StyleData style : line) {
                builder.append(style);
            }
        }

        return builder.toString();
    }
    private final List<StyleData[]> buffer = new ArrayList<>();

    public void addParagraph(StyleData[] style) {
        buffer.add(style);
    }

    public StyleData[] getParagraph(int paragraph) {
        return buffer.get(paragraph);
    }

    public int size() {
        return buffer.size();
    }

    public void clear() {
        buffer.clear();
    }
}
