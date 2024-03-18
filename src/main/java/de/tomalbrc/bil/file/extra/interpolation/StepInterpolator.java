package de.tomalbrc.bil.file.extra.interpolation;

import org.joml.Vector3f;

public class StepInterpolator implements Interpolator {
    public Vector3f interpolate(float progress, Vector3f[] points) {
        return new Vector3f(progress > 0.5f ? points[1] : points[0]);
    }
}
