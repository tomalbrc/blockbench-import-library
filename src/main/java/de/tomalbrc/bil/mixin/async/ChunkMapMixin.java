package de.tomalbrc.bil.mixin.async;

import de.tomalbrc.bil.core.holder.base.BaseElementHolder;
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

@Mixin(value = ChunkMap.class, priority = 900)
public class ChunkMapMixin implements IChunkMap {
    @Unique
    private ObjectArrayList<BaseElementHolder> resin$scheduledAsyncTicks = new ObjectArrayList<>();
    @Unique
    @Nullable
    private CompletableFuture<Void> resin$asyncTickFuture;

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void resin$afterTickEntityTrackers(CallbackInfo ci) {
        ObjectArrayList<BaseElementHolder> holders = this.resin$scheduledAsyncTicks;
        if (holders.isEmpty()) {
            this.resin$asyncTickFuture = null;
            return;
        }

        this.resin$scheduledAsyncTicks = new ObjectArrayList<>(holders.size());
        this.resin$asyncTickFuture = CompletableFuture.runAsync(() -> {
            for (BaseElementHolder holder : holders) {
                holder.asyncTick();
            }
        });
    }

    @Override
    public void resin$scheduleAsyncTick(BaseElementHolder holder) {
        this.resin$scheduledAsyncTicks.add(holder);
    }

    @Override
    public void resin$blockUntilAsyncTickFinished() {
        if (this.resin$asyncTickFuture != null && !this.resin$asyncTickFuture.isDone()) {
            // Makes sure that all the async ticks have finished.
            this.resin$asyncTickFuture.join();
        }
    }
}
