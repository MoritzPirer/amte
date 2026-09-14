package Controller.Action;

import Controller.Action.Ranges.Range;
import Controller.Action.Ranges.RangeResolver;
import Controller.Services.Clipbobard.ClipboardHandler;
import Controller.Services.Clipbobard.CopyType;
import Controller.ViewPort;
import FileStorage.TextFile;
import Settings.SettingsMaster;

public class CopyAction extends Action {
    private final RangeResolver resolver;
    private final CopyType type;

    public CopyAction(RangeResolver resolver, CopyType type) {
        this.resolver = resolver;
        this.type = type;
    }

    @Override
    public void apply(ViewPort viewPort, TextFile file) {
        Range range = resolver.resolve(viewPort, file);
        String content = file.readRange(range.start(), range.end());

        viewPort.copy(type, content);

        if (SettingsMaster.instance().getBoolean("yankToSystemClipboard")) {
            ClipboardHandler.putSystemClipboardContent(content);
        }
    }
}
