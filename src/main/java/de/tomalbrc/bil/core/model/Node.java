package de.tomalbrc.bil.core.model;

import de.tomalbrc.bil.file.bbmodel.BbElement;
import de.tomalbrc.bil.file.bbmodel.BbGroup;
import de.tomalbrc.bil.file.bbmodel.BbOutliner;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record Node(@NotNull UUID uuid, @NotNull NodeType type, @NotNull NodeTag tag, @Nullable Node parent,
                   @NotNull Transform transform, @NotNull String name, @Nullable Identifier modelData,
                   @Nullable BbElement displayDataElement, @NotNull List<Node> children) {
    public void addChild(Node node) {
        this.children.add(node);
    }

    @Override
    public List<Node> children() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public @NotNull String toString() {
        return uuid.toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    // --- Inner Classes (Enums) ---

    public enum NodeType {
        BONE, LOCATOR, ITEM, BLOCK, TEXT
    }

    public enum NodeTag {
        NONE, HEAD, HEAD_CHILD, HITBOX, SEAT, DRIVER_SEAT;

        public static NodeTag of(String name) {
            if (name == null) return NONE;
            if (name.startsWith("head")) return HEAD;
            if (name.startsWith("hitbox")) return HITBOX;
            if (name.startsWith("seat")) return SEAT;
            if (name.startsWith("driver")) return DRIVER_SEAT;
            return NONE;
        }

        public boolean isHead() {
            return this == HEAD || this == HEAD_CHILD;
        }
    }

    public static class Builder {
        private UUID uuid;
        private NodeType type;
        private NodeTag tag;
        private Node parent;
        private Transform transform;
        private String name;
        private Identifier modelData;
        private BbElement displayDataElement;
        private final List<Node> children = new ArrayList<>();

        public Builder from(BbGroup group) {
            this.uuid = group.uuid;
            this.name = group.name;
            return this;
        }

        public Builder from(BbOutliner outliner) {
            this.uuid = outliner.uuid;
            this.name = outliner.name;
            return this;
        }

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder type(NodeType type) {
            this.type = type;
            return this;
        }

        public Builder tag(NodeTag tag) {
            this.tag = tag;
            return this;
        }

        public Builder parent(Node parent) {
            this.parent = parent;
            return this;
        }

        public Builder transform(Transform transform) {
            this.transform = transform;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder modelData(Identifier modelData) {
            this.modelData = modelData;
            return this;
        }

        public Builder displayDataElement(BbElement displayDataElement) {
            this.displayDataElement = displayDataElement;
            return this;
        }

        public Builder child(Node child) {
            this.children.add(child);
            return this;
        }

        public Node build() {
            if (uuid == null) throw new IllegalStateException("Node UUID is required");
            if (type == null) throw new IllegalStateException("Node Type is required");
            if (transform == null) throw new IllegalStateException("Node Transform is required");
            if (name == null) throw new IllegalStateException("Node Name is required");

            if (this.tag == null) {
                if (parent != null && parent.tag().isHead()) {
                    this.tag = NodeTag.HEAD_CHILD;
                } else {
                    this.tag = NodeTag.of(this.name);
                }
            }

            return new Node(uuid, type, tag, parent, transform, name, modelData, displayDataElement, children);
        }
    }
}