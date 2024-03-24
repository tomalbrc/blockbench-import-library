package de.tomalbrc.bil.core.model;

import de.tomalbrc.bil.file.extra.interpolation.CatmullRomInterpolator;
import de.tomalbrc.bil.file.extra.interpolation.Interpolator;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.sounds.SoundEvent;
import org.joml.Vector3f;

import java.util.List;
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
        once, hold, loop
    }
}
