package de.tomalbrc.bil.file.importer;

import de.tomalbrc.bil.file.bbmodel.BbElement;
import de.tomalbrc.bil.file.bbmodel.BbFace;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// silly and hacky rotation-fix for cubes with invalid rotation
// not needed in 1.21.11
public class BoxRotationBaker {
    private static final float ROT_STEP = 22.5f;

    public static void bakeRotation(BbElement element) {
        if (element.rotation == null) {
            element.rotation = new Vector3f(0, 0, 0);
            return;
        }

        snapToNearestInterval(element.rotation);

        if (needsBaking(element.rotation.y)) {
            bakeAxis(element, 0, 1, 0); // y axis
        } else if (needsBaking(element.rotation.x)) {
            bakeAxis(element, 1, 0, 0); // x axis
        } else if (needsBaking(element.rotation.z)) {
            bakeAxis(element, 0, 0, 1); // z axis
        }
    }

    private static boolean needsBaking(float angle) {
        return Math.abs(angle) > 45.f;
    }

    private static void snapToNearestInterval(Vector3f rot) {
        rot.x = Math.round(rot.x / ROT_STEP) * ROT_STEP;
        rot.y = Math.round(rot.y / ROT_STEP) * ROT_STEP;
        rot.z = Math.round(rot.z / ROT_STEP) * ROT_STEP;
    }

    private static void bakeAxis(BbElement element, float xDir, float yDir, float zDir) {
        float currentAngle = 0;
        if (yDir == 1) currentAngle = element.rotation.y;
        else if (xDir == 1) currentAngle = element.rotation.x;
        else if (zDir == 1) currentAngle = element.rotation.z;

        int steps = Math.round(currentAngle / 90.0f);
        float bakeAngle = steps * 90.0f;
        float residualAngle = currentAngle - bakeAngle;

        if (steps == 0) return;

        Vector3f origin = element.origin != null ? element.origin : new Vector3f(0,0,0);

        float rads = (float) Math.toRadians(bakeAngle);

        Matrix4f mat = new Matrix4f()
                .translate(origin)
                .rotate(rads, xDir, yDir, zDir)
                .translate(new Vector3f(origin).negate());

        Vector3f min = element.from;
        Vector3f max = element.to;

        List<Vector3f> corners = new ArrayList<>();
        corners.add(new Vector3f(min.x, min.y, min.z));
        corners.add(new Vector3f(max.x, min.y, min.z));
        corners.add(new Vector3f(min.x, max.y, min.z));
        corners.add(new Vector3f(max.x, max.y, min.z));
        corners.add(new Vector3f(min.x, min.y, max.z));
        corners.add(new Vector3f(max.x, min.y, max.z));
        corners.add(new Vector3f(min.x, max.y, max.z));
        corners.add(new Vector3f(max.x, max.y, max.z));

        Vector3f newMin = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
        Vector3f newMax = new Vector3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);

        for (Vector3f corner : corners) {
            Vector4f v4 = new Vector4f(corner.x, corner.y, corner.z, 1.0f);
            v4.mul(mat);
            newMin.min(new Vector3f(v4.x, v4.y, v4.z));
            newMax.max(new Vector3f(v4.x, v4.y, v4.z));
        }

        element.from = newMin;
        element.to = newMax;

        if (yDir == 1) element.rotation.y = residualAngle;
        else if (xDir == 1) element.rotation.x = residualAngle;
        else if (zDir == 1) element.rotation.z = residualAngle;

        if (element.faces != null) {
            int rotationsNeeded;

            if (xDir == 1) {
                rotationsNeeded = steps;
            } else {
                rotationsNeeded = -steps;
            }

            while (rotationsNeeded < 0) rotationsNeeded += 4;
            rotationsNeeded = rotationsNeeded % 4;

            for (int i = 0; i < rotationsNeeded; i++) {
                if (yDir == 1) rotateFacesY(element);
                else if (xDir == 1) rotateFacesX(element);
                else if (zDir == 1) rotateFacesZ(element);
            }
        }
    }

    private static void rotateFacesY(BbElement e) {
        Map<String, BbFace> old = new HashMap<>(e.faces);
        Map<String, BbFace> next = e.faces;

        moveFace(old, next, "north", "east");
        moveFace(old, next, "east", "south");
        moveFace(old, next, "south", "west");
        moveFace(old, next, "west", "north");

        rotateUV(next.get("up"), 90);
        rotateUV(next.get("down"), -90);
    }

    private static void rotateFacesX(BbElement e) {
        Map<String, BbFace> old = new HashMap<>(e.faces);
        Map<String, BbFace> next = e.faces;

        moveFace(old, next, "north", "up");
        moveFace(old, next, "up", "south");
        moveFace(old, next, "south", "down");
        moveFace(old, next, "down", "north");

        rotateUV(next.get("east"), 90);
        rotateUV(next.get("west"), -90);
    }

    private static void rotateFacesZ(BbElement e) {
        Map<String, BbFace> old = new HashMap<>(e.faces);
        Map<String, BbFace> next = e.faces;

        moveFace(old, next, "up", "east");
        moveFace(old, next, "east", "down");
        moveFace(old, next, "down", "west");
        moveFace(old, next, "west", "up");

        rotateUV(next.get("north"), 90);
        rotateUV(next.get("south"), -90);
    }

    private static void moveFace(Map<String, BbFace> source, Map<String, BbFace> dest, String fromKey, String toKey) {
        if (source.containsKey(fromKey)) {
            dest.put(toKey, source.get(fromKey));
        } else {
            dest.remove(toKey);
        }
    }

    private static void rotateUV(BbFace face, int angle) {
        if (face == null) return;
        int current = face.rotation;
        current += angle;
        while (current < 0) current += 360;
        face.rotation = current % 360;
    }
}