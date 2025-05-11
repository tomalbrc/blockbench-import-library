package de.tomalbrc.bil.core.model;

import de.tomalbrc.bil.file.bbmodel.BbElement;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.util.UUID;

public record Node(
        @NotNull NodeType type,
        @NotNull Node parent,
        @NotNull Transform transform,
        @NotNull String name,
        @NotNull UUID uuid,
        @Nullable ResourceLocation modelData,
        boolean headTag,
        @Nullable BbElement displayDataElement
) {
    public enum NodeType {
        BONE,
        LOCATOR,
        ITEM,
        BLOCK,
        TEXT
    }

    public static final class Transform {
        private final Vector3f origin;
        private final Quaternionf rotation;
        private final float scale;
        private Matrix4f globalTransform;

        public Transform(Vector3f origin, Quaternionf rotation, float scale) {
            this.origin = origin;
            this.rotation = rotation;
            this.scale = scale;
            this.globalTransform = new Matrix4f().translate(origin).rotate(rotation);
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

        public Quaternionfc rotation() {
            return rotation;
        }

        public float scale() {
            return scale;
        }

        public Matrix4fc globalTransform() {
            return globalTransform;
        }
    }
}
