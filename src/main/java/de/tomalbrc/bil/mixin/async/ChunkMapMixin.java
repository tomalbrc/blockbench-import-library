package de.tomalbrc.bil.mixin.async;

import de.tomalbrc.bil.core.holder.base.AbstractElementHolder;
import de.tomalbrc.bil.util.IChunkMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ChunkMap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

import static de.tomalbrc.bil.BIL.EXECUTOR;

@Mixin(value = ChunkMap.class, priority = 900)
public class ChunkMapMixin implements IChunkMap {
    @Unique
    private ObjectArrayList<AbstractElementHolder> bil$scheduledAsyncTicks = new ObjectArrayList<>();
    @Unique
    @Nullable
    private CompletableFuture<Void> bil$asyncTickFuture;

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void bil$afterTickEntityTrackers(CallbackInfo ci) {
        ObjectArrayList<AbstractElementHolder> holders = this.bil$scheduledAsyncTicks;
        if (holders.isEmpty()) {
            this.bil$asyncTickFuture = null;
            return;
        }

        this.bil$scheduledAsyncTicks = new ObjectArrayList<>(holders.size());
        this.bil$asyncTickFuture = CompletableFuture.runAsync(() -> {
            for (AbstractElementHolder holder : holders) {
                holder.asyncTick();
            }
        }, EXECUTOR);
    }

    @Override
    public void bil$scheduleAsyncTick(AbstractElementHolder holder) {
        this.bil$scheduledAsyncTicks.add(holder);
    }

    @Override
    public void bil$blockUntilAsyncTickFinished() {
        if (this.bil$asyncTickFuture != null && !this.bil$asyncTickFuture.isDone()) {
            // Makes sure that all the async ticks have finished.
            this.bil$asyncTickFuture.join();
        }
    }
}
