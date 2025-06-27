package de.tomalbrc.bil.core.holder.base;

import de.tomalbrc.bil.util.IChunkMap;
import de.tomalbrc.bil.util.Utils;
import eu.pb4.polymer.virtualentity.api.attachment.HolderAttachment;
import eu.pb4.polymer.virtualentity.api.elements.VirtualElement;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for all holder that handles Polymer's ElementHolder specific logic.
 * <p>
 * This class mainly exists to split off ElementHolder logic from the element logic.
 */
public abstract class AbstractElementHolder extends NetworkEfficientElementHolder {
    protected ServerGamePacketListenerImpl[] watchingPlayers;
    protected boolean elementsInitialized;
    protected boolean isDataLoaded;

    @Deprecated(forRemoval = true)
    protected AbstractElementHolder(ServerLevel unused) {
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

        var result = super.startWatching(player);
        this.watchingPlayers = this.getWatchingPlayers().toArray(Utils.EMPTY_CONNECTION_ARRAY);
        return result;
    }

    @Override
    public boolean stopWatching(ServerGamePacketListenerImpl player) {
        boolean result = super.stopWatching(player);
        this.watchingPlayers = this.getWatchingPlayers().toArray(Utils.EMPTY_CONNECTION_ARRAY);
        return result;
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
        this.onAsyncTick();

        for (VirtualElement element : this.getElements()) {
            element.tick();
        }

        flushPackets(watchingPlayers);
    }

    public @Nullable ServerLevel getLevel() {
        return this.getAttachment() != null ? this.getAttachment().getWorld() : null;
    }
}
