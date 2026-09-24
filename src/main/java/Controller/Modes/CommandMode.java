package Controller.Modes;


import Controller.Action.*;
import Controller.Input;
import Controller.SpecialInput;
import Controller.ViewPort;
import FileStorage.TextFile;
import LanguageSupport.LanguageProvider;

import java.util.HashMap;
import java.util.Map;

public class CommandMode implements ModeState {
    private final StringBuilder buffer;
    private final ModeManager modeManager;
    private final Map<String, Action> simpleCommands;
    private final LanguageProvider provider = new LanguageProvider();

    public CommandMode(ModeManager modeManager) {
        this.buffer = new StringBuilder();
        this.modeManager = modeManager;
        this.simpleCommands = createSimpleCommands();
    }

    private Map<String, Action> createSimpleCommands() {
        Map<String, Action> commands = new HashMap<>();

        addWithAliases(commands, new SaveAction(), new String[]{"save", "s", "write", "w"});
        addWithAliases(commands, QuitAction.makeForceQuit(), new String[]{"exit", "forcequit", "force-quit", "force quit", "e"});
        addWithAliases(commands, QuitAction.makeSoftQuit(), new String[]{"quit", "q", "close"});
        addWithAliases(commands, new CompoundAction(new SaveAction(), QuitAction.makeSoftQuit()), new String[]{"save quit", "sq", "save-quit", "wq"});

        return commands;
    }

    private void addWithAliases(Map<String, Action> commands, Action command, String[] aliases) {
        for (String alias : aliases) {
            commands.put(alias, command);
        }
    }

    @Override
    public Action parseInput(Input input, ViewPort viewPort, TextFile file) {
        if (input.isSpecialInput()) {
            switch (input.getSpecialInput()) {
                case SpecialInput.ESCAPE -> {
                    modeManager.setMode(new ToolMode(modeManager));
                    viewPort.clearTemporaryMessages();
                    return new NullAction();
                }
                case ENTER -> {
                    return processCommand();
                }
                case BACK_SPACE -> {
                    if (buffer.isEmpty()) {
                        modeManager.setMode(new ToolMode(modeManager));
                        viewPort.clearTemporaryMessages();
                        return new NullAction();
                    }
                    buffer.deleteCharAt(buffer.length() - 1);
                }
            }
        }
        else {
            buffer.append(input.getCharacter());
        }
        return showUnfinishedCommand();
    }

    private Action processCommand() {
        String command = buffer.toString();
        buffer.delete(0, buffer.length());

        modeManager.setMode(new ToolMode(modeManager));

        if (simpleCommands.containsKey(command)) {
            return simpleCommands.get(command);
        }

        if (!command.contains(" ")) {
            return new NotifyAction(provider.get("command/unknown"));
        }

        return processWithArguments(command);
    }

    private Action processWithArguments(String command) {
        int firstSpace = command.indexOf(' ');
        assert firstSpace != -1: "control flow should not allow a command without a space to reach this point";

        String keyword = command.substring(0, firstSpace);
        String arguments = command.substring(firstSpace + 1);

        return switch (keyword) {
            case "rename", "r" -> new RenameAction(arguments);
            default -> new NotifyAction(provider.get("command/unknown"));
        };
    }

    private Action showUnfinishedCommand() {
        return new NotifyAction(":" + buffer);
    }

    @Override
    public String getName() {
        return new LanguageProvider().get("ui/mode/command");
    }
}
