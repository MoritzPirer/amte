package Rendering;

public interface Renderer {

    /**
     * starts the rendering thread
     */
    void start();

    /**
     * stops the rendering thread
     */
    void stop();

    /**
     * returns the Style of the given paragraph. If the paragraph does not exist (e.g. because
     * the file changed since the last complete rendering pass), an empty array is returned
     * @param paragraph the paragraph to get the StyleData for
     * @return the StyleData of the paragraph
     */
    StyleData[] getStyle(int paragraph);

    /**
     * inform the rendering thread of a change of the file type so it can select the correct rendering mode
     */
    void registerFileTypeChange(String newFileExtension);

    void requestRefresh();
}