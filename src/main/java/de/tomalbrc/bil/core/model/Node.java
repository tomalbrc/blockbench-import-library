package de.tomalbrc.bil.core.model;

import de.tomalbrc.bil.file.bbmodel.BbElement;
import de.tomalbrc.bil.util.Utils;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record Node(
        @NotNull NodeType type,
        @Nullable Node parent,
        @NotNull Transform transform,
        @NotNull String name,
        @NotNull UUID uuid,
        @Nullable Identifier modelData,
        boolean headTag,
        @Nullable BbElement displayDataElement,
        @NotNull List<Node> children
) {
    public void addChild(Node node) {
        children.add(node);
    }

    public List<Node> children() {
        return Collections.unmodifiableList(children);
    }

    public enum NodeType {
        BONE,
        LOCATOR,
        ITEM,
        BLOCK,
        TEXT
    }

    public static final class Transform {
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
}
