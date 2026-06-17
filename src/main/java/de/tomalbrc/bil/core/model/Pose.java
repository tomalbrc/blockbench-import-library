package de.tomalbrc.bil.core.model;

import com.mojang.math.MatrixUtil;
import org.joml.*;

public record Pose(
        Vector3f translation,
        Vector3f scale,
        Quaternionf leftRotation,
        Quaternionf rightRotation,
        Matrix4fc matrix
) {
    public static Pose of(Matrix4f matrix4f) {
        Vector3f translation = new Vector3f();
        Quaternionf leftRotation = new Quaternionf();
        Vector3f scale = new Vector3f();
        Quaternionf rightRotation = new Quaternionf();

        MatrixUtil.svdDecompose(matrix4f, translation, leftRotation, scale, rightRotation);

        return new Pose(translation, scale, leftRotation, rightRotation, matrix4f);
    }

    public Vector3fc readOnlyTranslation() {
        return this.translation;
    }

    public Vector3fc readOnlyScale() {
        return this.scale;
    }

    public Quaternionfc readOnlyLeftRotation() {
        return this.leftRotation;
    }

    public Quaternionfc readOnlyRightRotation() {
        return this.rightRotation;
    }

    @Override
    public Vector3f translation() {
        return new Vector3f(this.translation);
    }

    @Override
    public Vector3f scale() {
        return new Vector3f(this.scale);
    }

    @Override
    public Quaternionf leftRotation() {
        return new Quaternionf(this.leftRotation);
    }

    @Override
    public Quaternionf rightRotation() {
        return new Quaternionf(this.rightRotation);
    }
}
