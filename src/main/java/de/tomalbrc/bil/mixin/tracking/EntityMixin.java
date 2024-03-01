package de.tomalbrc.bil.mixin.tracking;

import de.tomalbrc.bil.api.AjEntity;
import de.tomalbrc.bil.api.AjEntityHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Shadow
    private EntityDimensions dimensions;

    @Inject(method = "refreshDimensions", at = @At("RETURN"))
    private void resin$onRefreshedDimensions(CallbackInfo ci) {
        AjEntityHolder holder = AjEntity.getHolder(this);
        if (holder != null) {
            holder.onDimensionsUpdated(this.dimensions);
        }
    }
}
