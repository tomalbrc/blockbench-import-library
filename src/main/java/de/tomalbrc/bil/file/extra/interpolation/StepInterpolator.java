package de.tomalbrc.bil.file.extra.interpolation;

import org.joml.Vector3f;

public class StepInterpolator implements Interpolator {
    @Override
    public Vector3f interpolate(float t, Vector3f beforePlus, Vector3f before, Vector3f after, Vector3f afterPlus) {
        return new Vector3f(t >= 1.0f ? after : before);
    }
}
