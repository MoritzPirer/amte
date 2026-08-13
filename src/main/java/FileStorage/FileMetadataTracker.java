package FileStorage;


public interface FileMetadataTracker {
    /**
     * @return the number of words in the buffer
     */
    long getWordCount();

    /**
     * @return the number of characters in the buffer
     */
    long getCharacterCount();

    /**
     * @return the number of paragraphs in the buffer
     */
    long getParagraphCount();

    /**
     * @return the FileBuffer object that this object tracks metadata of
     */
    FileBuffer getTrackedFileBuffer();
}
