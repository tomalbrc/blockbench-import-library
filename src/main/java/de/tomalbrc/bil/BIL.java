package de.tomalbrc.bil;

import com.mojang.logging.LogUtils;
import de.tomalbrc.bil.command.BILCommand;
import de.tomalbrc.bil.util.ResourcePackUtil;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import gg.moonflower.molangcompiler.api.MolangCompiler;
import net.minecraft.server.MinecraftServer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mod("bil_neo")
public class BIL {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static MolangCompiler COMPILER = MolangCompiler.create(MolangCompiler.DEFAULT_FLAGS, BIL.class.getClassLoader());
    public static ExecutorService EXECUTOR = Executors.newWorkStealingPool();
    public static MinecraftServer SERVER;

    public BIL() {
        NeoForge.EVENT_BUS.addListener(this::onServerStarting);
        NeoForge.EVENT_BUS.addListener(this::onServerStopped);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);

        PolymerResourcePackUtils.RESOURCE_PACK_AFTER_INITIAL_CREATION_EVENT.register(ResourcePackUtil::addAdditional);
    }

    private void onServerStarting(ServerStartingEvent event) {
        SERVER = event.getServer();
    }

    private void onServerStopped(ServerStoppedEvent event) {
        EXECUTOR.shutdownNow();
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        BILCommand.register(event.getDispatcher());

    }
}