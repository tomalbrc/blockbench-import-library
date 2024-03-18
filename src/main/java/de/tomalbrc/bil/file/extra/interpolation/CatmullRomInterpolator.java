package de.tomalbrc.bil.file.extra.interpolation;

import de.tomalbrc.bil.file.extra.interpolation.Interpolator;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class CatmullRomInterpolator implements Interpolator {
    public Vector3f interpolate(float t, Vector3f[] points) {
        int numPoints = points.length;

        // Handle cases with 2 control points using binary Catmull-Rom
        if (numPoints == 2) {
            return binaryCatmullRom(points[0], points[1], t);
        }

        // Handle general cases with 3 or more control points
        int p0 = (int) Math.floor(t);
        int p1 = p0 + 1;
        t = t - p0;

        // Handle edge cases for the first and last segments
        if (p0 == 0) p0 = 1;
        if (p1 == numPoints - 1) p1 = numPoints - 2;

        Vector3f t1 = points[p0 - 1].mul(-0.5f).add(points[p0]).add(points[p1]).mul(0.5f);
        Vector3f t2 = points[p0].mul(-0.5f).add(points[p1]).add(points[p1 + 1]).mul(0.5f);

        Vector3f a = points[p0 - 1].mul(2).add(points[p0]).sub(points[p1]);
        Vector3f b = points[p0].mul(3).sub(points[p0 - 1]).sub(points[p1]).sub(points[p1 + 1]);
        Vector3f c = points[p1 + 1].sub(points[p0]).add(t1).mul(2);

        Vector3f tSquared = new Vector3f(t * t);
        Vector3f tCubed = new Vector3f(tSquared.mul(t));

        return a.mul(tCubed).add(b.mul(tSquared)).add(c.mul(t)).add(t2);
    }


    @NotNull
    public Vector3f interpolate(Vector3f before, Vector3f from, Vector3f to, Vector3f after, float progress) {
        int numPoints = Math.min(4, Math.addExact(Math.addExact(2, before != null ? 1 : 0), after != null ? 1 : 0));
        if (numPoints == 2 && before == null && after == null) {
            return binaryCatmullRom(from, to, progress);
        }

        Vector3f[] points = new Vector3f[numPoints];
        points[1] = from;
        points[2] = to;
        int offset = 0;
        if (before != null) {
            points[offset++] = before;
        }
        if (after != null) {
            points[offset + 1] = after;
        }

        float scaledProgress = (progress + (before != null ? 1.0f : 0.0f)) / (numPoints - 1);
        return catmullRom(scaledProgress, points);
    }

    static float ALPHA = 0.5f;

    private static Vector3f binaryCatmullRom(Vector3f p0, Vector3f p1, float t) {
        float t2 = t * t;
        float t3 = t2 * t;

        float a1 = (2.0f * t3 - 3.0f * t2 + 1.0f);
        float a2 = (-(t3 - 2.0f * t2 + t) * ALPHA);
        return new Vector3f(
                a1 * p0.x + a2 * (p1.x - p0.x),
                a1 * p0.y + a2 * (p1.y - p0.y),
                a1 * p0.z + a2 * (p1.z - p0.z)
        );
    }

    private static Vector3f catmullRom(float t, Vector3f[] points) {
        int numPoints = points.length;
        int intPoint = Math.floorMod((int) Math.floor(t * (numPoints - 1)), numPoints);

        double weight = t * (numPoints - 1) - intPoint;
        double t2 = weight * weight;
        double t3 = weight * t2;

        Vector3f a1 = points[intPoint == 0 ? intPoint : intPoint - 1];
        Vector3f a2 = points[intPoint];
        Vector3f a3 = points[intPoint == numPoints - 1 ? intPoint : intPoint + 1];
        Vector3f a4 = points[intPoint == numPoints - 2 ? intPoint : intPoint + 2];

        return new Vector3f(
                catmullRomComponent(weight, t2, t3, a1.x, a2.x, a3.x, a4.x),
                catmullRomComponent(weight, t2, t3, a1.y, a2.y, a3.y, a4.y),
                catmullRomComponent(weight, t2, t3, a1.z, a2.z, a3.z, a4.z)
        );
    }

    private static float catmullRomComponent(double weight, double t2, double t3, float p0, float p1, float p2, float p3) {
        return (float) (0.5f * ((2.0f * p2 - 2.0f * p1) + (p0 - p3) * ALPHA * weight) * t3 +
                (-p2 + p1) * t2 + (2.0f * p1 - p0) * weight + p0);
    }
}
