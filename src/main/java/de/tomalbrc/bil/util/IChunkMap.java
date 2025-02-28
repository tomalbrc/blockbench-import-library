package de.tomalbrc.bil.util;

import de.tomalbrc.bil.core.holder.base.AbstractElementHolder;
import net.minecraft.server.level.ChunkMap;

public interface IChunkMap {
    static void scheduleAsyncTick(AbstractElementHolder holder) {
        IChunkMap chunkMap = (IChunkMap) holder.getLevel().getChunkSource().chunkMap;
        chunkMap.bil$scheduleAsyncTick(holder);
    }

    static void blockUntilAsyncTickFinished(ChunkMap map) {
        IChunkMap chunkMap = (IChunkMap) map;
        chunkMap.bil$blockUntilAsyncTickFinished();
    }

    void bil$scheduleAsyncTick(AbstractElementHolder holder);

    void bil$blockUntilAsyncTickFinished();
}
