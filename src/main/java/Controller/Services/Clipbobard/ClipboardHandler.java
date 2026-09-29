package Controller.Services.Clipbobard;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public class ClipboardHandler {

    private CopyType type = null;
    private String content = null;

    public ClipboardHandler() {}
    public ClipboardHandler(CopyType type, String content) {
        this.type = type;
        this.content = content;
    }


    public CopyType getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public void copy(CopyType type, String content) {
        this.type = type;
        this.content = content;
    }

    /**
     * attempts to retrieve the content of the system clipboard and return them as a string.
     * @return the contents of the system clipboard, or null if the clipboard is empty, not text, or otherwise unusable
     * @see <a href="https://www.baeldung.com/java-clipboard-copy-paste-text#2-retrieving-from-clipboard">...</a>
     */
    public static String getSystemClipboardContent() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(null);

        try {
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            }

            return null;
        }
        catch (UnsupportedFlavorException | IOException e) {
            return null;
        }
    }

    /**
     * copies the given content into the system clipboard
     * @param content the content to copy
     * @see <a href="https://www.baeldung.com/java-clipboard-copy-paste-text#2-retrieving-from-clipboard">...</a>
     */
    public static void setSystemClipboardContent(String content) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(content);
        clipboard.setContents(selection, null);
    }
}
