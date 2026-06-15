package de.tomalbrc.bil.core.element;

import eu.pb4.polymer.virtualentity.api.data.SimpleSynchedEntityData;
import eu.pb4.polymer.virtualentity.api.data.SynchedEntityDataLike;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PerPlayerItemDisplayElement extends ItemDisplayElement implements PerPlayerTransformableElement {
    final Map<ServerPlayer, Data> playerDataTracker = new ConcurrentHashMap<>();

    public PerPlayerItemDisplayElement(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public void startWatching(ServerPlayer player, Consumer<Packet<? super ClientGamePacketListener>> packetConsumer) {
        this.playerDataTracker.put(player, new Data());
        super.startWatching(player, packetConsumer);
    }

    @Override
    public void stopWatching(ServerPlayer player, Consumer<Packet<? super ClientGamePacketListener>> packetConsumer) {
        this.playerDataTracker.remove(player);
        super.stopWatching(player, packetConsumer);
    }

    @Override
    public Map<ServerPlayer, Data> playerDataTrackers() {
        return playerDataTracker;
    }

    @Override
    protected void sendTrackerUpdates() {
        sendTrackerUpdatesPerPlayer();
    }

    @Override
    public SynchedEntityDataLike createSyncedData() {
        return new SimpleSynchedEntityData(this.getEntityType());
    }
}
