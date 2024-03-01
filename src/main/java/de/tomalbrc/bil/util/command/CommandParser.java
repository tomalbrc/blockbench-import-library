package de.tomalbrc.bil.util.command;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

public final class CommandParser {
    private static final ParsedCommand[] EMPTY_COMMAND_ARRAY = new ParsedCommand[0];

    public static ParsedCommand[] parse(String commandString) {
        return parse(commandString, null);
    }

    public static ParsedCommand[] parse(String commandString, @Nullable String prefix) {
        String[] commands = commandString.trim().split("(\r\n|\r|\n)", -1);

        ObjectArrayList<ParsedCommand> list = new ObjectArrayList<>(commands.length);
        for (String command : commands) {
            String sanitized = sanitizeCommand(command, prefix);
            if (sanitized != null) {
                list.add(new ParsedCommand(sanitized));
            }
        }

        return list.toArray(EMPTY_COMMAND_ARRAY);
    }

    @Nullable
    private static String sanitizeCommand(String command, @Nullable String prefix) {
        command = command.trim();
        if (command.isEmpty()) {
            return null;
        }

        if (command.charAt(0) == '/') {
            command = command.substring(1);
            if (command.isEmpty()) {
                return null;
            }
        }

        if (prefix != null) {
            command = prefix + command;
        }

        return command;
    }
}
