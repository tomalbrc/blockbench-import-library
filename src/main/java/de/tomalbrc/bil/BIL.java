package de.tomalbrc.bil;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.loader.BbModelLoader;
import de.tomalbrc.bil.util.RPUtil;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import de.tomalbrc.bil.command.BILCommand;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

public class BIL implements ModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> {
            BILCommand.register(dispatcher);
        });

        PolymerResourcePackUtils.RESOURCE_PACK_AFTER_INITIAL_CREATION_EVENT.register(RPUtil::addAdditional);

        System.out.println("Starting test");
        Stopwatch stopwatch = Stopwatch.createStarted();

        for (int i = 0; i < 10; i++) {
            System.out.println("Run " + i);
            Model m = new BbModelLoader().load("generic_test");
            Model m2 = new BbModelLoader().load("chest-example");
            Model m3 = new BbModelLoader().load("armor_stand");
            Model m4 = new BbModelLoader().load("bonnie");
            new BbModelLoader().load("salamander");
            new BbModelLoader().load("snail");
            new BbModelLoader().load("stone_golem");
            new BbModelLoader().load("stone_minion");
            new BbModelLoader().load("wraith");
            new BbModelLoader().load("kobold_archer");
            //new BBModelLoader().load("kobold_warrior");
            new BbModelLoader().load("lava_salamander");
            new BbModelLoader().load("mimic");
            new BbModelLoader().load("plant_monster");
        }

        stopwatch.stop();
        long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("Execution time: " + elapsedTime + " milliseconds");
    }
}
