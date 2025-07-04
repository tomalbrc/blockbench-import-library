package de.tomalbrc.bil;

import com.mojang.logging.LogUtils;
import de.tomalbrc.bil.command.BILCommand;
import de.tomalbrc.bil.util.ResourcePackUtil;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import gg.moonflower.molangcompiler.api.MolangCompiler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

public class BIL implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static MolangCompiler COMPILER = MolangCompiler.create(MolangCompiler.DEFAULT_FLAGS, BIL.class.getClassLoader());

    public static MinecraftServer SERVER;

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> BILCommand.register(dispatcher));
        ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> SERVER = minecraftServer);
        PolymerResourcePackUtils.RESOURCE_PACK_AFTER_INITIAL_CREATION_EVENT.register(ResourcePackUtil::addAdditional);
    }
}
