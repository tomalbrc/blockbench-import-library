package de.tomalbrc.bil.util.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import de.tomalbrc.bil.BIL;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ParsedCommand {
    private static final CommandSourceStack PARSING_SOURCE = new CommandSourceStack(
            CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, null, 2, "", CommonComponents.EMPTY, null, null
    );
    private final String command;
    @Nullable
    private ParseResults<CommandSourceStack> parsed;
    private boolean isInvalid;

    protected ParsedCommand(String command) {
        this.command = command;
    }

    public int execute(CommandDispatcher<CommandSourceStack> dispatcher, CommandSourceStack source) {
        if (this.parsed == null) {
            this.parsed = dispatcher.parse(this.command, PARSING_SOURCE);

            if (Commands.getParseException(this.parsed) != null) {
                BIL.LOGGER.error("Unable to parse command: {}", this.command);
                this.isInvalid = true;
            }
        }

        if (!this.isInvalid) {
            try {
                return dispatcher.execute(Commands.mapSource(this.parsed, (s) -> source));
            } catch (Throwable ignored) {
            }
        }
        return 0;
    }
}
