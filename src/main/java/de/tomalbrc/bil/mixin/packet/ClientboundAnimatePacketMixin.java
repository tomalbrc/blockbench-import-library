package de.tomalbrc.bil.mixin.packet;

import de.tomalbrc.bil.api.AjEntity;
import de.tomalbrc.bil.api.AjEntityHolder;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.network.protocol.game.ClientboundAnimatePacket.CRITICAL_HIT;
import static net.minecraft.network.protocol.game.ClientboundAnimatePacket.MAGIC_CRITICAL_HIT;

@Mixin(ClientboundAnimatePacket.class)
public class ClientboundAnimatePacketMixin {
    @Mutable
    @Shadow
    @Final
    private int id;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/Entity;I)V", at = @At("RETURN"))
    private void resin$modifyAnimatePacket(Entity entity, int action, CallbackInfo ci) {
        AjEntityHolder holder = AjEntity.getHolder(entity);
        if (holder != null) {
            this.id = switch (action) {
                // Return the entity id for handling critical hit particles.
                case CRITICAL_HIT, MAGIC_CRITICAL_HIT -> holder.getCritParticleId();
                // Return invalid entity id to prevent the client from incorrectly type casting.
                default -> -1;
            };
        }
    }
}
