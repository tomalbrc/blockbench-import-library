package de.tomalbrc.bil.file.importer;

import de.tomalbrc.bil.file.bbmodel.BbKeyframe;
import de.tomalbrc.bil.file.bbmodel.BbOutliner;
import de.tomalbrc.bil.file.extra.BbVariablePlaceholders;
import de.tomalbrc.bil.file.extra.interpolation.Interpolation;
import de.tomalbrc.bil.file.extra.interpolation.Interpolator;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Vector3f;

import java.util.List;

class Sampler {
    private static Vector3f interpolateKeyframeChannelAt(List<BbKeyframe> keyframes, BbKeyframe.Channel channel, BbVariablePlaceholders placeholders, Interpolation interpolator, MolangEnvironment environment, float time) throws MolangRuntimeException {
        if (keyframes == null || keyframes.isEmpty()) {
            return null;
        }

        // Find the closest keyframes before and after the target time, including those before "before" and after "after"
        BbKeyframe before = null;
        BbKeyframe beforeBefore = null;
        BbKeyframe after = null;
        BbKeyframe afterAfter = null;

        float closestBeforeTime = Float.NEGATIVE_INFINITY;
        float closestBeforeBeforeTime = Float.NEGATIVE_INFINITY;
        float closestAfterTime = Float.POSITIVE_INFINITY;
        float closestAfterAfterTime = Float.POSITIVE_INFINITY;

        for (BbKeyframe keyframe : keyframes) {
            if (keyframe.channel == channel) {
                float keyframeTime = keyframe.time;

                if (keyframeTime <= time) {
                    // Update closest before keyframes
                    if (keyframeTime >= closestBeforeTime) {
                        beforeBefore = before;
                        closestBeforeBeforeTime = closestBeforeTime;
                        before = keyframe;
                        closestBeforeTime = keyframeTime;
                    } else if (keyframeTime > closestBeforeBeforeTime) {
                        beforeBefore = keyframe;
                        closestBeforeBeforeTime = keyframeTime;
                    }
                } else {
                    // Update closest after keyframes
                    if (keyframeTime <= closestAfterTime) {
                        afterAfter = after;
                        closestAfterAfterTime = closestAfterTime;
                        after = keyframe;
                        closestAfterTime = keyframeTime;
                    } else if (keyframeTime < closestAfterAfterTime) {
                        afterAfter = keyframe;
                        closestAfterAfterTime = keyframeTime;
                    }
                }
            }
        }

        if (before == null) {
            return null; // Can't interpolate
        }

        if (after == null) {
            return before.getVector3f(0, placeholders, environment);
        }

        float t = (time - before.time) / (after.time - before.time);

//        ObjectArrayList<Vector3f> points = new ObjectArrayList<>();
//        if (beforeBefore != null) points.add(beforeBefore.getVector3f(0, placeholders, environment));
//        points.add(before.getVector3f(0, placeholders, environment));
//        points.add(after.getVector3f(0, placeholders, environment));
//        if (afterAfter != null) points.add(afterAfter.getVector3f(0, placeholders, environment));
//        var arr = points.toArray(points.toArray(new Vector3f[points.size()]));

        var arr2 = new Vector3f[]{before.getVector3f(0, placeholders, environment), after.getVector3f(0, placeholders, environment)};

        return interpolator.get().interpolate(t, arr2);
    }

    public static Triple<Vector3f, Vector3f, Vector3f> sample(List<BbKeyframe> keyframes, BbVariablePlaceholders placeholders, MolangEnvironment environment, float time) throws MolangRuntimeException {
        Vector3f pos = interpolateKeyframeChannelAt(keyframes, BbKeyframe.Channel.position, placeholders, Interpolation.SMOOTH, environment, time);
        Vector3f rot = interpolateKeyframeChannelAt(keyframes, BbKeyframe.Channel.rotation, placeholders, Interpolation.SMOOTH, environment, time);
        Vector3f scale = interpolateKeyframeChannelAt(keyframes, BbKeyframe.Channel.scale, placeholders, Interpolation.SMOOTH, environment, time);

        if (pos == null) pos = new Vector3f();
        if (rot == null) rot = new Vector3f();
        if (scale == null) scale = new Vector3f(1, 1, 1);

        return Triple.of(pos, rot, scale);
    }
}
