package de.tomalbrc.bil;

import com.mojang.logging.LogUtils;
import de.tomalbrc.bil.file.loader.BBModelLoader;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.util.RPUtil;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
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

        PolymerResourcePackUtils.RESOURCE_PACK_AFTER_INITIAL_CREATION_EVENT.register(RPUtil::addAdditional);
    }
}
