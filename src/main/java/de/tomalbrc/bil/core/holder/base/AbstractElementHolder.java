package de.tomalbrc.bil.core.holder.base;

import de.tomalbrc.bil.BIL;
import de.tomalbrc.bil.util.IChunkMap;
import de.tomalbrc.bil.util.Utils;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for all holder that handles Polymer's ElementHolder specific logic.
 * <p>
 * This class mainly exists to split off ElementHolder logic from the element logic.
 */
public abstract class AbstractElementHolder extends NetworkEfficientElementHolder {
    private ServerGamePacketListenerImpl[] watchingPlayers;
    private boolean elementsInitialized;
    private boolean isDataLoaded;

    @Deprecated(forRemoval = true)
    protected AbstractElementHolder(ServerLevel level) {
        this();
    }

    protected AbstractElementHolder() {
        super();
        this.watchingPlayers = Utils.EMPTY_CONNECTION_ARRAY;
    }

    abstract protected void initializeElements();

    abstract protected void onAsyncTick();

    abstract protected void onDataLoaded();

    abstract protected boolean shouldSkipTick();

    @Override
    public void setAttachment(@Nullable HolderAttachment attachment) {
        if (attachment != null && !this.elementsInitialized) {
            this.elementsInitialized = true;
            this.initializeElements();
        }
        super.setAttachment(attachment);
    }

    @Override
    public boolean startWatching(ServerGamePacketListenerImpl player) {
        if (!this.isDataLoaded) {
            this.isDataLoaded = true;
            this.onDataLoaded();
        }

        return super.startWatching(player);
    }

    @Override
    public final void tick() {
        if (this.getAttachment() == null || this.shouldSkipTick()) {
            return;
        }

        this.onTick();

        this.updatePosition();

        // Schedule an async tick for this holder.
        IChunkMap.scheduleAsyncTick(this);
    }

    public final void asyncTick() {
        this.watchingPlayers = this.getWatchingPlayers().toArray(this.watchingPlayers);

        this.onAsyncTick();

        for (VirtualElement element : this.getElements()) {
            element.tick();
        }

        flushPackets();
    }

    public void sendPacketDirect(ServerGamePacketListenerImpl player, Packet<? extends ClientGamePacketListener> packet) {
        if (player != null) {
            if (BIL.SERVER.isSameThread()) {
                super.sendPacketDirect(player, packet);
            } else {
                Utils.sendPacketNoFlush(player, packet);
            }
        }
    }

    public @Nullable ServerLevel getLevel() {
        return this.getAttachment() != null ? this.getAttachment().getWorld() : null;
    }
}
