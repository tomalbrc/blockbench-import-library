package de.tomalbrc.bil.mixin.async;

import de.tomalbrc.bil.util.IChunkMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow
    public abstract Iterable<ServerLevel> getAllLevels();

    @Inject(
            method = "tickChildren",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    ordinal = 4
            )
    )
    private void bil$ensureAsyncTickFinished(BooleanSupplier booleanSupplier, CallbackInfo ci) {
        // Block here at the end of the gametick just before packet flushing is resumed until all the async element updates have completed.
        // This way we can take advantage of suspending packet flushing, which gives a significant improvement in network performance and ping.
        // This will never realistically block the main thread, as these async model ticks are done at the same time as entity ticks on the main thread,
        // which in any normal world will take far longer than the model updates. This is just a safety measure.
        for (ServerLevel level : this.getAllLevels()) {
            IChunkMap.blockUntilAsyncTickFinished(level.getChunkSource().chunkMap);
        }
    }
}
