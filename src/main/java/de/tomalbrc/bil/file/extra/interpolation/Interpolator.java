package de.tomalbrc.bil.file.extra.interpolation;

import org.joml.Vector3f;

public interface Interpolator {
    public Vector3f interpolate(float t, Vector3f beforePlus, Vector3f before, Vector3f after, Vector3f afterPlus);
}
