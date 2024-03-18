package de.tomalbrc.bil.mixin.tracking;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.api.AnimatedEntityHolder;
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
    private void bil$onRefreshedDimensions(CallbackInfo ci) {
        AnimatedEntityHolder holder = AnimatedEntity.getHolder(this);
        if (holder != null) {
            holder.onDimensionsUpdated(this.dimensions);
        }
    }
}
