package de.tomalbrc.bil.file.importer;

import de.tomalbrc.bil.file.bbmodel.Keyframe;
import de.tomalbrc.bil.file.bbmodel.Outliner;
import de.tomalbrc.bil.file.bbmodel.VariablePlaceholders;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.ast.ObjectAwareExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.omega.arcane.parser.MolangParser;
import dev.omega.arcane.reference.ExpressionBindingContext;
import dev.omega.arcane.reference.ReferenceType;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

class Sampler {

    private static void f(MolangExpression expression, Float time) {
        ExpressionBindingContext context = ExpressionBindingContext.create();

        context.registerDirectReferenceResolver(ReferenceType.QUERY, "life_time", () -> {return  time;});
        context.registerDirectReferenceResolver(ReferenceType.QUERY, "anim_time", () -> {return  time;});
        context.registerDirectReferenceResolver(ReferenceType.VARIABLE, "mech_mul", () -> {return  time;});

        expression.bind(context, time);
    }


    private static Vector3f interpolateKeyframeChannelAt(List<Keyframe> keyframes, Keyframe.Channel channel, VariablePlaceholders placeholders, float time) {
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
                    before.dataPoints.get(0).get("x").getValue(placeholders, time),
                    before.dataPoints.get(0).get("y").getValue(placeholders, time),
                    before.dataPoints.get(0).get("z").getValue(placeholders, time));
        }

        float t = (time - before.time) / (after.time - before.time);

        Vector3f beforeValue = new Vector3f(
                before.dataPoints.get(0).get("x").getValue(placeholders, time),
                before.dataPoints.get(0).get("y").getValue(placeholders, time),
                before.dataPoints.get(0).get("z").getValue(placeholders, time));
        Vector3f afterValue = new Vector3f(
                after.dataPoints.get(0).get("x").getValue(placeholders, time),
                after.dataPoints.get(0).get("y").getValue(placeholders, time),
                after.dataPoints.get(0).get("z").getValue(placeholders, time));

        return beforeValue.lerp(afterValue, t);
    }

    public static Triple<Vector3f, Vector3f, Vector3f> sample(Outliner bone, List<Keyframe> keyframes, VariablePlaceholders placeholders, float time) {
        Vector3f pos = interpolateKeyframeChannelAt(keyframes, Keyframe.Channel.position, placeholders, time);
        Vector3f rot = interpolateKeyframeChannelAt(keyframes, Keyframe.Channel.rotation, placeholders, time);
        Vector3f scale = interpolateKeyframeChannelAt(keyframes, Keyframe.Channel.scale, placeholders, time);

        if (pos == null) pos = new Vector3f();
        if (rot == null) rot = new Vector3f();
        if (scale == null) scale = new Vector3f(1, 1, 1);

        return Triple.of(pos, rot, scale);
    }
}
