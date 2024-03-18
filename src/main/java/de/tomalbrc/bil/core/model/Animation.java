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


    // TODO
    static class AnimationTrack {
        interface Keyframe<T> {
            public T interpolate(float t);
        }

        static class TransformKeyframe implements Keyframe<Vector3f> {
            final Vector3f[] points;
            final Interpolator interpolator;

            TransformKeyframe(Vector3f[] points, Interpolator interpolator) {
                this.points = points;
                this.interpolator = interpolator;
            }

            public Vector3f interpolate(float t) {
                return interpolator.interpolate(t, points);
            }
        }
        static class EffectKeyframe implements Keyframe<EffectData> {
            final SoundEvent soundEvent;
            final String command;

            EffectKeyframe(SoundEvent soundEvent, String command) {
                this.soundEvent = soundEvent;
                this.command = command;
            }

            public EffectData interpolate(float t) {
                //return Interpolator.smooth();
                return null;
            }
        }

        static record EffectData(SoundEvent sound, String command, String other) {
        }
    }
}
