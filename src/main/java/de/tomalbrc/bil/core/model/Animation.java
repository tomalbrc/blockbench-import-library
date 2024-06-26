package de.tomalbrc.bil.core.model;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

import java.util.UUID;

public record Animation(
        Frame[] frames,
        int startDelay,
        int loopDelay,

        int duration,
        LoopMode loopMode,
        ReferenceOpenHashSet<UUID> affectedBones,
        boolean affectedBonesIsAWhitelist
) {

    public boolean isAffected(UUID boneUuid) {
        return this.affectedBonesIsAWhitelist == this.affectedBones.contains(boneUuid);
    }

    public enum LoopMode {
        ONCE,
        HOLD,
        LOOP
    }
}
