package de.tomalbrc.bil;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import de.tomalbrc.bil.command.BILCommand;
import org.slf4j.Logger;

public class BIL implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
            BILCommand.register(dispatcher);
        });
    }
}
