package de.tomalbrc.bil;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import de.tomalbrc.bil.command.BILCommand;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.extra.interpolation.CatmullRomInterpolator;
import de.tomalbrc.bil.file.loader.BbModelLoader;
import de.tomalbrc.bil.util.RPUtil;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import gg.moonflower.molangcompiler.api.MolangCompiler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.joml.Vector3f;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

public class BIL implements ModInitializer {
    public static MolangCompiler COMPILER = MolangCompiler.create(MolangCompiler.DEFAULT_FLAGS, BIL.class.getClassLoader());

    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
            BILCommand.register(dispatcher);
        });

        PolymerResourcePackUtils.RESOURCE_PACK_AFTER_INITIAL_CREATION_EVENT.register(RPUtil::addAdditional);
    }
}
