package de.tomalbrc.bil.file.extra.interpolation;

import org.joml.Vector3f;

public class LinerInterpolator implements Interpolator {
    public Vector3f interpolate(float progress, Vector3f[] points) {
        return points[0].lerp(points[1], progress, new Vector3f());
    }
}
