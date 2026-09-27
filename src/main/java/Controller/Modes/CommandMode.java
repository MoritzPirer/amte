package Controller.Modes;


import Controller.Action.*;
import Controller.Input;
import Controller.SpecialInput;
import Controller.TemporaryMessages.InputCountDuration;
import Controller.TemporaryMessages.Message;
import Controller.TemporaryMessages.MessageType;
import Controller.TemporaryMessages.TimeDuration;
import Controller.ViewPort;
import FileStorage.TextFile;
import LanguageSupport.LanguageProvider;

import java.util.*;

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
        addWithAliases(commands, QuitAction.makeForceQuit(), new String[]{"exit", "forcequit", "force-quit", "force quit", "fq", "e"});
        addWithAliases(commands, QuitAction.makeSoftQuit(), new String[]{"quit", "q", "close"});
        addWithAliases(commands, new CompoundAction(new SaveAction(), QuitAction.makeSoftQuit()), new String[]{"save quit", "sq", "save-quit", "wq"});
        commands.put("list", makeListCommandAction());

        return commands;
    }

    private void addWithAliases(Map<String, Action> commands, Action command, String[] aliases) {
        for (String alias : aliases) {
            commands.put(alias, command);
        }
    }

    private Action makeListCommandAction() {
        String list = provider.get("command/list");
        String[] names = list.split("\n");
        Arrays.sort(names);

        List<Action> actions = new ArrayList<>(names.length);
        for (String name : names) {
            actions.add(new NotifyAction(new Message(name, MessageType.INFO, new InputCountDuration(1))));
        }

        return new CompoundAction(actions);
    }

    @Override
    public Action parseInput(Input input, ViewPort viewPort, TextFile file) {
        if (input.isSpecialInput()) {
            switch (input.getSpecialInput()) {
                case SpecialInput.ESCAPE -> {
                    modeManager.setMode(new ToolMode(modeManager));
                    viewPort.refreshTemporaryMessages();
                    return new NullAction();
                }
                case ENTER -> {
                    return processCommand();
                }
                case BACK_SPACE -> {
                    if (buffer.isEmpty()) {
                        modeManager.setMode(new ToolMode(modeManager));
                        viewPort.refreshTemporaryMessages();
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
            return new NotifyAction(new Message(provider.get("command/unknown"), MessageType.ERROR, new InputCountDuration(2)));
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
            case "info" -> makeInfoCommand(arguments);
            case "help" -> makeHelpCommand(arguments);
            case "find", "f" -> makeFindCommand(arguments, false);
            case "FIND", "F" -> makeFindCommand(arguments, true);
            default -> new NotifyAction(new Message(provider.get("command/unknown"), MessageType.ERROR, new InputCountDuration(2)));
        };
    }

    private Action makeInfoCommand(String argument) {
        Set<String> validArguments = Set.of(
            "save", "force-quit", "quit", "save-quit", "rename", "help", "info", "list"
        );

        if (validArguments.contains(argument)) {
            return new NotifyAction(new Message(
                provider.get("command/info/" + argument),
                MessageType.INFO,
                new InputCountDuration(1)
            ));
        }

        return new NotifyAction(new Message(provider.get("command/unknown"), MessageType.ERROR, new InputCountDuration(2)));
    }

    private Action makeHelpCommand(String argument) {
        String validTools = "aAbBcCdDeEfFhiIjJkloOpPstTuUwWxXyY:?";

        if (argument.length() == 1 && validTools.contains(argument)) {
            return new NotifyAction(new Message(provider.get("command/help/" + argument), MessageType.INFO, new InputCountDuration(1)));
        }

        return new NotifyAction(new Message(provider.get("command/unknown"), MessageType.ERROR, new InputCountDuration(1)));
    }

    private Action makeFindCommand(String argument, boolean reversed) {
        if (argument.isEmpty()) {
            return new NotifyAction(new Message(provider.get("command/find/empty"), MessageType.ERROR, TimeDuration.seconds(2)));
        }
        modeManager.setLastFindTarget(argument);
        return new FindAction(argument, reversed);
    }

    private Action showUnfinishedCommand() {
        return new NotifyAction(new Message(":" + buffer,  MessageType.INFO, new InputCountDuration(1)));
    }

    @Override
    public String getName() {
        return provider.get("ui/mode/command");
    }
}
