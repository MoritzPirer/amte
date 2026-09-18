package Rendering;

import java.util.ArrayList;
import java.util.List;

class RenderingBuffer {
    private final List<StyleData[]> buffer = new ArrayList<>();

    /**
     * append a paragraph to the end of the buffer
     * @param style the paragraph's style
     */
    public void addParagraph(StyleData[] style) {
        buffer.add(style);
    }

    /**
     * read a paragraph from the buffer
     * @param paragraph the paragraph to read. non-existing paragraphs must be handled externally
     * @return the Styling of the specified paragraph
     */
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
