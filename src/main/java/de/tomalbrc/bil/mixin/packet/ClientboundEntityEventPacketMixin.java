package de.tomalbrc.bil.mixin.packet;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.api.AnimatedEntityHolder;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientboundEntityEventPacket.class)
public class ClientboundEntityEventPacketMixin {
    @Mutable
    @Shadow
    @Final
    private int entityId;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/Entity;B)V", at = @At("RETURN"))
    private void resin$modifyEventPacket(Entity entity, byte b, CallbackInfo ci) {
        AnimatedEntityHolder holder = AnimatedEntity.getHolder(entity);
        if (holder != null) {
            this.entityId = holder.getEntityEventId();
        }
    }
}
