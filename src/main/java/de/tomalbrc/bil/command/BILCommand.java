package de.tomalbrc.bil.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class BILCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        var builder = Commands.literal("bil");
        builder.then(ModelCommand.register());
        dispatcher.register(builder);
    }
}
