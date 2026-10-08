package FileStorage;


public interface FileMetadataTracker {
    /**
     * @return the number of words in the buffer
     */
    int getWordCount();

    /**
     * @return the number of characters in the buffer
     */
    int getCharacterCount();

    /**
     * @return the number of paragraphs in the buffer
     */
    int getParagraphCount();

    /**
     * @return the FileBuffer object that this object tracks metadata of
     */
    FileBuffer getTrackedFileBuffer();
}
