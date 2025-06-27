package de.tomalbrc.bil.core.element;

import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.tracker.DataTrackerLike;
import eu.pb4.polymer.virtualentity.api.tracker.SimpleDataTracker;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

public class PerPlayerItemDisplayElement extends ItemDisplayElement implements PerPlayerTransformableElement {
    Map<ServerPlayer, Data> playerDataTracker = new Object2ReferenceOpenHashMap<>();

    public PerPlayerItemDisplayElement(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public void startWatching(ServerPlayer player, Consumer<Packet<ClientGamePacketListener>> packetConsumer) {
        this.playerDataTracker.put(player, new Data());
        super.startWatching(player, packetConsumer);
    }

    @Override
    public void stopWatching(ServerPlayer player, Consumer<Packet<ClientGamePacketListener>> packetConsumer) {
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
    public DataTrackerLike createDataTracker() {
        return new SimpleDataTracker(this.getEntityType());
    }
}
