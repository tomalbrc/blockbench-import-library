package de.tomalbrc.bil.mixin.accessor;

import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerGamePacketListenerImpl.class)
public interface ServerCommonPacketListenerImplAccessor {
    @Accessor
    Connection getConnection();
}
