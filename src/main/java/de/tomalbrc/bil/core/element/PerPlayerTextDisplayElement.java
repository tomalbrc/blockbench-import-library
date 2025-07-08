package de.tomalbrc.bil.core.element;

import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import eu.pb4.polymer.virtualentity.api.tracker.DataTrackerLike;
import eu.pb4.polymer.virtualentity.api.tracker.SimpleDataTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PerPlayerTextDisplayElement extends TextDisplayElement implements PerPlayerTransformableElement {
    final Map<ServerPlayer, Data> playerDataTracker = new ConcurrentHashMap<>();

    public PerPlayerTextDisplayElement(Component text) {
        super(text);
    }

    @Override
    public Map<ServerPlayer, Data> playerDataTrackers() {
        return playerDataTracker;
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
    protected void sendTrackerUpdates() {
        sendTrackerUpdatesPerPlayer();
    }

    public DataTrackerLike createDataTracker() {
        return new SimpleDataTracker(this.getEntityType());
    }
}
