package de.tomalbrc.bil.util;

import de.tomalbrc.bil.holder.base.BaseElementHolder;
import net.minecraft.server.level.ChunkMap;

public interface IChunkMap {
    void resin$scheduleAsyncTick(BaseElementHolder holder);

    void resin$blockUntilAsyncTickFinished();

    static void scheduleAsyncTick(BaseElementHolder holder) {
        IChunkMap chunkMap = (IChunkMap) holder.getLevel().getChunkSource().chunkMap;
        chunkMap.resin$scheduleAsyncTick(holder);
    }

    static void blockUntilAsyncTickFinished(ChunkMap map) {
        IChunkMap chunkMap = (IChunkMap) map;
        chunkMap.resin$blockUntilAsyncTickFinished();
    }
}
