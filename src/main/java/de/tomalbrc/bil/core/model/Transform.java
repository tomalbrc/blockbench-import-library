package de.tomalbrc.bil.core.model;

import de.tomalbrc.bil.util.Utils;
import org.joml.*;

public final class Transform {
    private final Vector3fc origin;
    private final Vector3fc rotation;
    private final float localScale;

    private Matrix4f globalTransform;

    public Transform(Vector3f origin, Vector3fc rotation, float localScale) {
        this.origin = origin;
        this.rotation = rotation;
        this.localScale = localScale;
        this.globalTransform = new Matrix4f().translate(origin).rotate(Utils.createQuaternion(rotation));
    }

    public Transform mul(Transform other) {
        this.globalTransform = other.globalTransform.mul(this.globalTransform, new Matrix4f());
        return this;
    }

    public Transform mul(Matrix4f other) {
        this.globalTransform = other.mul(this.globalTransform, new Matrix4f());
        return this;
    }

    public Vector3fc origin() {
        return origin;
    }

    public Vector3fc rotation() {
        return rotation;
    }

    public float localScale() {
        return localScale;
    }

    public Matrix4fc globalTransform() {
        return globalTransform;
    }
}
