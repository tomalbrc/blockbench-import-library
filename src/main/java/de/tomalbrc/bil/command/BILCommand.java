package de.tomalbrc.bil.command;

import com.mojang.brigadier.CommandDispatcher;
import de.tomalbrc.bil.util.Permissions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class BILCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var builder = Commands.literal("bil").requires(Permissions.require("bil.command", 4));
        builder.then(ModelCommand.register());
        dispatcher.register(builder);
    }
}
