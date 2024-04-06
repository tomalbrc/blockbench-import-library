package de.tomalbrc.bil.file.importer;

import de.tomalbrc.bil.file.bbmodel.BbKeyframe;
import de.tomalbrc.bil.file.extra.BbVariablePlaceholders;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Vector3f;

import java.util.Iterator;
import java.util.List;

class Sampler {
    private static Vector3f interpolateKeyframeChannelAt(Iterator<BbKeyframe> iterator, BbVariablePlaceholders placeholders, MolangEnvironment environment, float time) throws MolangRuntimeException {
        // Find the closest keyframes before and after the target time, including those before "before" and after "after"
        BbKeyframe before = null;
        BbKeyframe beforeBefore = null;
        BbKeyframe after = null;
        BbKeyframe afterAfter = null;

        while (iterator.hasNext()) {
            BbKeyframe currentFrame = iterator.next();
            if (currentFrame.time <= time) {
                if (before == null || currentFrame.time > before.time) {
                    beforeBefore = before;
                    before = currentFrame;
                }
            } else {
                after = currentFrame;
                if (iterator.hasNext()) {
                    afterAfter = iterator.next();
                }
                break;
            }
        }

        if (before == null) {
            return after != null ?
                    after.getVector3f(0, placeholders, environment) : null; // Can't interpolate
        }
        else if (after == null) {
            return before.getVector3f(0, placeholders, environment);
        }

        if (before.time > 0 && beforeBefore == null) {
            beforeBefore = before;
        }

        float t = (time - before.time) / (after.time - before.time);
        return before.interpolation.get().interpolate(t,
                beforeBefore == null ? null : beforeBefore.getVector3f(0, placeholders, environment),
                before == null ? null : before.getVector3f(0, placeholders, environment),
                after == null ? null : after.getVector3f(0, placeholders, environment),
                afterAfter == null ? null : afterAfter.getVector3f(0, placeholders, environment));
    }

    public static Triple<Vector3f, Vector3f, Vector3f> sample(List<BbKeyframe> keyframes, BbVariablePlaceholders placeholders, MolangEnvironment environment, float time) throws MolangRuntimeException {
        Vector3f pos = null;
        Vector3f rot = null;
        Vector3f scale = null;

        if (keyframes != null && !keyframes.isEmpty()) {
            var pi = keyframes.stream().filter(x -> x.channel == BbKeyframe.Channel.position).sorted().iterator();
            var ri = keyframes.stream().filter(x -> x.channel == BbKeyframe.Channel.rotation).sorted().iterator();
            var si = keyframes.stream().filter(x -> x.channel == BbKeyframe.Channel.scale).sorted().iterator();

            pos = interpolateKeyframeChannelAt(pi, placeholders, environment, time);
            rot = interpolateKeyframeChannelAt(ri, placeholders, environment, time);
            scale = interpolateKeyframeChannelAt(si, placeholders, environment, time);
        }

        if (pos == null) pos = new Vector3f();
        else pos.mul(-1,1,-1);

        if (rot == null) rot = new Vector3f();
        if (scale == null) scale = new Vector3f(1, 1, 1);

        return Triple.of(pos, rot, scale);
    }
}
