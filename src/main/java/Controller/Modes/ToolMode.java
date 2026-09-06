package Controller.Modes;

import Controller.Action.*;
import Controller.Action.Ranges.Range;
import Controller.Action.Ranges.RangeResolver;
import Controller.Action.Ranges.RangeResolverFactory;
import Controller.Action.Ranges.ResolverState;
import Controller.Input;
import Controller.SpecialInput;
import Controller.ViewPort;
import FileStorage.TextFile;
import LanguageSupport.LanguageProvider;
import Shared.Direction;
import Shared.TextPosition;

class ToolMode implements ModeState {
    private static final int MAX_CHORD_LENGTH = 3;

    private final ModeManager modeManager;
    private final StringBuilder buffer = new StringBuilder();

    public ToolMode(ModeManager modeManager) {
        this.modeManager = modeManager;
    }

    @Override
    public Action parseInput(Input input, ViewPort viewPort, TextFile file) {
        if (input.isSpecialInput()) {
            return parseSpecialInput(input.getSpecialInput(), viewPort);
        }

        buffer.append(input.getCharacter());
        Action action = tryConvertBuffer(viewPort, file);
        if (action == null) {
            if (buffer.length() == MAX_CHORD_LENGTH) {
                buffer.delete(0, buffer.length());
            }
            return new NullAction();
        }

        return action;
    }

    //NOTE: viewPort will be used by later commands
    private Action parseSpecialInput(SpecialInput input, ViewPort viewPort) {
        switch (input) {
            case ARROW_LEFT -> {
                return new MoveAction(Direction.LEFT);
            }
            case ARROW_DOWN -> {
                return new MoveAction(Direction.DOWN);
            }
            case ARROW_UP -> {
                return new MoveAction(Direction.UP);
            }
            case ARROW_RIGHT -> {
                return new MoveAction(Direction.RIGHT);
            }
            default -> {
                buffer.delete(0, buffer.length());
                return new NullAction();
            }
        }
    }

    private Action tryConvertBuffer(ViewPort viewPort, TextFile file) {
        if (buffer.length() == 1) {
            Action action = parseCharacterInput(buffer.charAt(0), viewPort, file);
            if (action != null) {
                buffer.delete(0, buffer.length());
                return action;
            }
            return new NullAction();
        }

        char operator = buffer.charAt(0);
        String arguments = buffer.substring(1);

        return parseAsChord(operator, arguments, viewPort, file);
    }

    private Action parseCharacterInput(char character, ViewPort viewPort, TextFile file) {
        switch (character) {
            case 'a' -> {
                modeManager.setMode(new TypingMode(modeManager));
                return new MoveAction(Direction.RIGHT);
            }
            case 'i' -> {
                modeManager.setMode(new TypingMode(modeManager));
                return new NullAction();
            }
            case 'h' -> {
                return new MoveAction(Direction.LEFT);
            }
            case 'j' -> {
                return new MoveAction(Direction.DOWN);
            }
            case 'k' -> {
                return new MoveAction(Direction.UP);
            }
            case 'l' -> {
                return new MoveAction(Direction.RIGHT);
            }
            case 's' -> {
                modeManager.setMode(new TypingMode(modeManager));
                if (file.getBuffer().isEmpty()) {
                    return new NullAction();
                }
                
                TextPosition cursor = viewPort.getLogicalCursorPosition();
                TextPosition next = file.nextPosition(cursor);
                return new DeleteAction(cursor, next);
            }
            case 'x' -> {
                if (file.getBuffer().isEmpty()) {
                    return new NullAction();
                }

                TextPosition cursor = viewPort.getLogicalCursorPosition();
                TextPosition next = file.nextPosition(cursor);
                return new DeleteAction(cursor, next);
            }
            case 'X' -> {
                TextPosition cursor = viewPort.getLogicalCursorPosition();
                TextPosition previous = file.previousPosition(cursor);
                if (previous == null) {
                    return new NullAction();
                }

                return new DeleteAction(previous, cursor);
            }

            case ':' -> {
                modeManager.setMode(new CommandMode(modeManager));
                return new NotifyAction(":");
            }
            default -> {
                return null;
            }
        }
    }

    private Action parseAsChord(char operator, String arguments, ViewPort viewPort, TextFile file) {
        switch (RangeResolverFactory.evaluate(operator, arguments)) {
            case INVALID -> {
                buffer.delete(0, buffer.length());
                return new NullAction();
            }
            case UNFINISHED -> {
                return new NotifyAction(buffer.toString());
            }
        }

        RangeResolver resolver = RangeResolverFactory.create(operator, arguments, viewPort, file);

        assert resolver != null;

        Range range = resolver.resolve(viewPort, file);
        buffer.delete(0, buffer.length());

        switch (operator) {
            case 'c': modeManager.setMode(new TypingMode(modeManager)); //fallthrough
            case 'd': {
                return new DeleteAction(range.first(), file.nextPosition(range.last()));
            }
            // copy action will go here
        }

        return new NullAction();
    }

    @Override
    public String getName() {
        LanguageProvider provider = new LanguageProvider();
        return provider.get("modeIdentifierTool");
    }
}
