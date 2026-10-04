package Controller.Modes;

import Controller.Action.*;
import Controller.Action.Ranges.FixedRangeResolver;
import Controller.Input;
import Controller.Services.Clipbobard.ClipboardHandler;
import Controller.SpecialInput;
import Controller.ViewPortManager.ViewPort;
import FileStorage.TextFile;
import LanguageSupport.LanguageProvider;
import Shared.TextPosition;

public class TypingMode implements ModeState {
    private final ModeManager modeManager;

    public TypingMode(ModeManager modeManager) {
        this.modeManager = modeManager;
    }

    @Override
    public Action parseInput(Input input, ViewPort viewPort, TextFile file) {
        if (input.isCharacter()) {
            return new InsertAction(String.valueOf(input.getCharacter()), viewPort.getLogicalCursorPosition());
        }

        return parseSpecialInput(input.getSpecialInput(), viewPort, file);
    }

    private Action parseSpecialInput(SpecialInput input, ViewPort viewPort, TextFile file) {
        switch (input) {
            case ESCAPE -> {
                modeManager.setMode(new ToolMode(modeManager));
                return new NullAction();
            }
            case BACK_SPACE -> { return createBackSpaceAction(viewPort, file); }
            case ENTER -> {
                return SmartList.trySmartListInsertion(new ParsingContext(viewPort, file, null));
            }
            case TAB -> { return createIndentAction(viewPort); }
            case SHIFT_TAB -> { return createUnindentAction(viewPort, file); }
            case PASTE -> { return createPasteAction(viewPort); }
            default -> { return new NullAction(); }
        }
    }

    private Action createIndentAction(ViewPort viewPort) {
        TextPosition position = new TextPosition(viewPort.getLogicalCursorPosition().paragraph(), 0);

        return new CompoundAction(
            new InsertAction("\t", position),
            new JumpAction(new FixedRangeResolver(null, viewPort.getLogicalCursorPosition().offset(0, 1)))
        );
    }

    private Action createUnindentAction(ViewPort viewPort, TextFile file) {
        int paragraphIndex = viewPort.getLogicalCursorPosition().paragraph();
        String paragraph = file.readRawParagraph(paragraphIndex);

        if (!paragraph.startsWith("\t")) {
            return new NullAction();
        }

        return new DeleteAction(
            new TextPosition(paragraphIndex, 0),
            new TextPosition(paragraphIndex, 1)
        );
    }

    private Action createPasteAction(ViewPort viewPort) {
        String content = ClipboardHandler.getSystemClipboardContent();
        if (content == null) {
            return new NullAction();
        }

        return new InsertAction(content, viewPort.getLogicalCursorPosition());
    }

    private Action createBackSpaceAction(ViewPort viewPort, TextFile file) {
        TextPosition cursor = viewPort.getLogicalCursorPosition();
        TextPosition previous = file.previousPosition(cursor);
        if (previous == null) {
            return new NullAction();
        }

        return new DeleteAction(previous, cursor);
    }

    @Override
    public String getName() {
        LanguageProvider provider = new LanguageProvider();
        return provider.get("ui/mode/typing");
    }
}
