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

public record Node(
        @NotNull UUID uuid,
        @NotNull NodeType type,
        @NotNull NodeTag tag,
        @Nullable Node parent,
        @NotNull Transform transform,
        @NotNull String name,
        @Nullable Identifier modelData,
        @Nullable BbElement displayDataElement,
        @NotNull List<Node> children
) {
    public void addChild(Node node) {
        children.add(node);
    }

    public List<Node> children() {
        return Collections.unmodifiableList(children);
    }

    public static Node of(NodeType type, BbGroup group, Identifier model, Node parent, Transform transform, BbElement displayDataElement) {
        return new Node(group.uuid, type, parent != null && parent.tag.isHead() ? NodeTag.HEAD_CHILD : NodeTag.of(group.name), parent, transform, group.name, model, displayDataElement, new ArrayList<>());
    }
    public static Node of(NodeType type, BbOutliner group, Identifier model, Node parent, Transform transform, BbElement displayDataElement) {
        return new Node(group.uuid, type, parent != null && parent.tag.isHead() ? NodeTag.HEAD_CHILD : NodeTag.of(group.name), parent, transform, group.name, model, displayDataElement, new ArrayList<>());
    }

    public static Node of(UUID uuid, NodeType type, BbGroup group, Identifier model, Node parent, Transform transform, BbElement displayDataElement) {
        return new Node(uuid, type, parent != null && parent.tag.isHead() ? NodeTag.HEAD_CHILD : NodeTag.of(group.name), parent, transform, group.name, model, displayDataElement, new ArrayList<>());
    }
    public static Node of(UUID uuid, NodeType type, BbOutliner group, Identifier model, Node parent, Transform transform, BbElement displayDataElement) {
        return new Node(uuid, type, parent != null && parent.tag.isHead() ? NodeTag.HEAD_CHILD : NodeTag.of(group.name), parent, transform, group.name, model, displayDataElement, new ArrayList<>());
    }

    public enum NodeType {
        BONE,
        LOCATOR,
        ITEM,
        BLOCK,
        TEXT
    }

    public enum NodeTag {
        NONE,
        HEAD,
        HEAD_CHILD,
        HITBOX,
        SEAT,
        DRIVER_SEAT;

        public static NodeTag of(String name) {
            if (name.startsWith("head")) {
                return HEAD;
            } else if (name.startsWith("hitbox")) {
                return HITBOX;
            } else if (name.startsWith("seat")) {
                return SEAT;
            } else if (name.startsWith("driver")) {
                return DRIVER_SEAT;
            }

            return NONE;
        }

        public boolean isHead() {
            return this == HEAD || this == HEAD_CHILD;
        }
    }
}
