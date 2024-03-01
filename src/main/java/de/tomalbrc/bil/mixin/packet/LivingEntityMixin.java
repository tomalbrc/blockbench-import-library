package de.tomalbrc.bil.mixin.packet;

import de.tomalbrc.bil.api.AjEntity;
import de.tomalbrc.bil.api.AjEntityHolder;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Redirect(
            method = "take",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;getId()I",
                    ordinal = 0
            )
    )
    private int resin$modifyPickupItemPacket(LivingEntity entity) {
        // Return the entity id for entity events to prevent the client from incorrectly type casting.
        // This assumes that the entity event id is of a LivingEntity on the client.
        AjEntityHolder holder = AjEntity.getHolder(entity);
        return holder != null ? holder.getEntityEventId() : entity.getId();
    }
}
