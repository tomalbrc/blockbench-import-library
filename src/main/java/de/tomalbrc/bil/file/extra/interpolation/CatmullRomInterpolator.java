package de.tomalbrc.bil.file.extra.interpolation;

import org.joml.Vector3f;

public class CatmullRomInterpolator implements Interpolator {
    public Vector3f interpolate(float t, Vector3f beforePlus, Vector3f before, Vector3f after, Vector3f afterPlus) {
        int i = 0;
        Vector3f arr[] = new Vector3f[2 + (beforePlus==null?0:1) + (afterPlus==null?0:1)];

        if (beforePlus != null) arr[i++] = beforePlus;
        arr[i++] = before;
        arr[i++] = after;
        if (afterPlus != null) arr[i] = afterPlus;

        return catmullRom((t + (beforePlus != null ? 1 : 0)) / (arr.length - 1), arr);
    }

    private static Vector3f catmullRom(float t, Vector3f... points) {
        final float factor = (points.length - 1) * t;

        int segment = (int) Math.floor(factor);

        Vector3f p0 = points[Math.max(segment - 1, 0)];
        Vector3f p1 = points[segment];
        Vector3f p2 = points[Math.min(segment + 1, points.length - 1)];
        Vector3f p3 = points[Math.min(segment + 2, points.length - 1)];

        float influence = factor - segment;

        float x = calculateCatmullRomSpline(influence, p0.x(), p1.x(), p2.x(), p3.x());
        float y = calculateCatmullRomSpline(influence, p0.y(), p1.y(), p2.y(), p3.y());
        float z = calculateCatmullRomSpline(influence, p0.z(), p1.z(), p2.z(), p3.z());

        return new Vector3f(x, y, z);
    }

    private static float calculateCatmullRomSpline(float t, float p0, float p1, float p2, float p3) {
        // first and second derivatives at p1 using neighboring control points
        float v0 = (p2 - p0) / 2.0f;
        float v1 = (p3 - p1) / 2.0f;

        // coefficients for cubic polynomial
        float a = 2.0f * p1 - 2.0f * p2 + v0 + v1;
        float b = -3.0f * p1 + 3.0f * p2 - 2.0f * v0 - v1;

        // eval cubic polynomial at t using Horner's rule
        return a * t * t * t + b * t * t + v0 * t + p1;
    }
}
