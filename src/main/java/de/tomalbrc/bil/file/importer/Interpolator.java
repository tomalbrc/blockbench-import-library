package de.tomalbrc.bil.file.importer;

import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class Interpolator {
    public static Vector3f linear(Vector3f a, Vector3f b, float progress) {
        return a.lerp(b, progress);
    }

    public static Vector3f step(Vector3f a, Vector3f b, float progress) {
        return new Vector3f(progress > 0.5f ? b : a);
    }

    public static Vector3f bezier(Vector3f a, Vector3f b, float progress) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public static Vector3f smooth(Vector3f aBefore, Vector3f a, Vector3f b, Vector3f bAfter, float progress) {
        Vector3f res = new Vector3f();
        for (int i = 0; i < 3; i++) {
            var value = Mth.catmullrom(progress, aBefore.get(i), a.get(i), b.get(i), bAfter.get(i));
            res.setComponent(i, value);
        }
        return res;
    }
}
