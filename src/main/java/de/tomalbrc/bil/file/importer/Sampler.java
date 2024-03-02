package de.tomalbrc.bil.file.importer;

import de.tomalbrc.bil.file.bbmodel.Keyframe;
import de.tomalbrc.bil.file.bbmodel.Outliner;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

class Sampler {
    private static Vector3f interpolateKeyframeChannelAt(List<Keyframe> keyframes, Keyframe.Channel channel, float time) {
        if (keyframes == null || keyframes.isEmpty()) {
            return null;
        }

        // Find the closest keyframes before and after the target time
        Keyframe before = null;
        Keyframe after = null;
        float closestBeforeTime = Float.NEGATIVE_INFINITY;
        float closestAfterTime = Float.POSITIVE_INFINITY;

        for (Keyframe keyframe : keyframes) {
            if (keyframe.channel == channel) {
                float keyframeTime = keyframe.time;
                if (keyframeTime <= time && keyframeTime > closestBeforeTime) {
                    before = keyframe;
                    closestBeforeTime = keyframeTime;
                } else if (keyframeTime > time && keyframeTime < closestAfterTime) {
                    after = keyframe;
                    closestAfterTime = keyframeTime;
                }
            }
        }

        if (before == null) {
            return null; // Can't interpolate
        }

        if (after == null) {
            return new Vector3f(
                    before.dataPoints.get(0).get("x"),
                    before.dataPoints.get(0).get("y"),
                    before.dataPoints.get(0).get("z"));
        }

        float t = (time - before.time) / (after.time - before.time);

        Vector3f beforeValue = new Vector3f(
                before.dataPoints.get(0).get("x"),
                before.dataPoints.get(0).get("y"),
                before.dataPoints.get(0).get("z"));
        Vector3f afterValue = new Vector3f(
                after.dataPoints.get(0).get("x"),
                after.dataPoints.get(0).get("y"),
                after.dataPoints.get(0).get("z"));

        return beforeValue.lerp(afterValue, t);
    }

    public static Matrix4f sample(Outliner bone, List<Keyframe> keyframes, float time) {
        Matrix4f matrix4f = new Matrix4f().identity();

        Vector3f off = bone.origin.mul(1 / 16.f, new Vector3f()).rotateY(Mth.PI);
        matrix4f.translate(off);

        Vector3f pos = interpolateKeyframeChannelAt(keyframes, Keyframe.Channel.position, time);
        Vector3f rot = interpolateKeyframeChannelAt(keyframes, Keyframe.Channel.rotation, time);
        Vector3f scale = interpolateKeyframeChannelAt(keyframes, Keyframe.Channel.scale, time);

        if (pos != null) matrix4f.translate(pos.rotateY(Mth.PI).mul(1 / 16.f));
        if (rot != null) {

            rot = rot.mul(Mth.DEG_TO_RAD);
            matrix4f.rotateXYZ(rot);

            //matrix4f.translate(off.negate());
        }
        if (scale != null) matrix4f.scale(scale);

        return matrix4f;
    }
}
