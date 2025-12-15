package de.tomalbrc.bil.core.model;

import de.tomalbrc.bil.util.Utils;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class Transform {
    private final Vector3fc origin;
    private final Vector3fc rotation;
    private final float scale;

    private Matrix4f globalTransform;

    public Transform(Vector3f origin, Vector3f rotation, float scale) {
        this.origin = origin;
        this.rotation = rotation;
        this.scale = scale;
        this.globalTransform = new Matrix4f().translate(origin).rotate(Utils.createQuaternion(rotation));
    }

    public Transform mul(Transform other) {
        this.globalTransform = other.globalTransform.mul(this.globalTransform, new Matrix4f());
        return this;
    }

    public Transform mul(Matrix4f other) {
        this.globalTransform = other.mul(this.globalTransform);
        return this;
    }

    public Vector3fc origin() {
        return origin;
    }

    public Vector3fc rotation() {
        return rotation;
    }

    public float scale() {
        return scale;
    }

    public Matrix4fc globalTransform() {
        return globalTransform;
    }
}
