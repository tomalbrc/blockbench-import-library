package de.tomalbrc.bil.core.model;

import com.mojang.math.MatrixUtil;
import org.joml.*;

import java.util.UUID;

public record Pose(
        Vector3f translation,
        Vector3f scale,
        Quaternionf leftRotation,
        Quaternionf rightRotation
) {
    public static Pose of(Matrix4f matrix4f) {
        Matrix3f matrix3f = new Matrix3f(matrix4f);
        Vector3f translation = matrix4f.getTranslation(new Vector3f());

        float multiplier = 1.0F / matrix4f.m33();
        if (multiplier != 1.0F) {
            matrix3f.scale(multiplier);
            translation.mul(multiplier);
        }

        var triple = MatrixUtil.svdDecompose(matrix3f);
        Vector3f scale = triple.getMiddle();
        Quaternionf leftRotation = triple.getLeft();
        Quaternionf rightRotation = triple.getRight();

        return new Pose(translation, scale, leftRotation, rightRotation);
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
