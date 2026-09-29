package Controller.Action;

import Controller.Action.Ranges.Range;
import Controller.Action.Ranges.RangeResolver;
import Controller.Services.Clipbobard.ClipboardHandler;
import Controller.Services.Clipbobard.CopyType;
import Controller.ViewPortManager.ViewPort;
import Controller.ViewPortManager.ViewPortManager;
import Settings.SettingsMaster;

public class CopyAction extends Action {
    private final RangeResolver resolver;
    private final CopyType type;

    public CopyAction(RangeResolver resolver, CopyType type) {
        this.resolver = resolver;
        this.type = type;
    }

    @Override
    public void apply(ViewPortManager viewPortManager) {
        ViewPort viewPort = viewPortManager.current();

        Range range = resolver.resolve(viewPort, viewPort.getFile());
        String content = viewPort.getFile().readRange(range.start(), range.end());

        viewPort.copy(type, content);

        if (SettingsMaster.instance().getBoolean("yankToSystemClipboard")) {
            ClipboardHandler.setSystemClipboardContent(content);
        }
    }
}
