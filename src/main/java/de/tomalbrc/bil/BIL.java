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

        System.out.println("Starting test");
        for (int ix = 0; ix < 10; ix++) {
            Stopwatch stopwatch = Stopwatch.createStarted();

            for (int i = 0; i < 10; i++) {
                //System.out.println("Run " + i);
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


            var interpolation = new CatmullRomInterpolator();
            interpolation.interpolate(0.1f, null, new Vector3f(0, 0, 0), new Vector3f(20, 20, 20), null);

            assert(new Vector3f(0, 0, 0).equals(interpolation.interpolate(0, null, new Vector3f(0, 0, 0), new Vector3f(20, 20, 20), null), 0.001f)); // from
            assert(new Vector3f(4.063F, 4.063F, 4.063F).equals(interpolation.interpolate(0.25f, null, new Vector3f(0, 0, 0), new Vector3f(20, 20, 20), null), 0.001f));
            assert(new Vector3f(10, 10, 10).equals(interpolation.interpolate(0.5f, null, new Vector3f(0, 0, 0), new Vector3f(20, 20, 20), null), 0.001f));
            assert(new Vector3f(15.938F, 15.938F, 15.938F).equals(interpolation.interpolate(0.75f, null, new Vector3f(0, 0, 0), new Vector3f(20, 20, 20), null), 0.001f));
            assert(new Vector3f(19.296F, 19.296F, 19.296F).equals(interpolation.interpolate(0.94f, null, new Vector3f(0, 0, 0), new Vector3f(20, 20, 20), null), 0.001f));
            assert(new Vector3f(20, 20, 20).equals(interpolation.interpolate(1f, null, new Vector3f(0, 0, 0), new Vector3f(20, 20, 20), null), 0.001f)); // to

            stopwatch.stop();
            long elapsedTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            System.out.println("Execution time: " + elapsedTime + " milliseconds");
        }
    }
}
