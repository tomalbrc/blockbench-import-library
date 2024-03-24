package de.tomalbrc.bil.file.extra.interpolation;

import org.joml.Vector3f;

public class LinerInterpolator implements Interpolator {
    @Override
    public Vector3f interpolate(float t, Vector3f beforePlus, Vector3f before, Vector3f after, Vector3f afterPlus) {
        return before.lerp(after, t, new Vector3f());
    }
}
