package de.tomalbrc.bil.mixin.tracking;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.api.AnimatedEntityHolder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SynchedEntityData.class)
public class SynchedEntityDataMixin {
    @Shadow
    @Final
    private Entity entity;

    @Inject(
            method = "set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V",
                    shift = At.Shift.AFTER
            )
    )
    private <T> void bil$onSetEntityData(EntityDataAccessor<T> key, T value, boolean force, CallbackInfo ci) {
        AnimatedEntityHolder holder = AnimatedEntity.getHolder(this.entity);
        if (holder != null) {
            holder.onSyncedDataUpdated(key, value);
        }
    }
}
