package de.tomalbrc.bil.core.model;

import com.google.gson.annotations.SerializedName;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import org.joml.*;

import java.util.UUID;

public record Node(
        NodeType type,
        Node parent,
        Transform transform,
        String name,
        UUID uuid,
        PolymerModelData modelData,
        boolean headTag
) {
    public enum NodeType {
        @SerializedName("bone")
        BONE,
        @SerializedName("locator")
        LOCATOR
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

        public Matrix4f globalTransform() {
            return globalTransform;
        }
    }
}
