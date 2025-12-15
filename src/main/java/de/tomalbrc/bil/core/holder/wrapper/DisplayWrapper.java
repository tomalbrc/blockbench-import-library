package de.tomalbrc.bil.core.holder.wrapper;

import de.tomalbrc.bil.core.element.PerPlayerTransformableElement;
import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;

public class DisplayWrapper<T extends PerPlayerTransformableElement> extends AbstractWrapper {
    private final T element;
    private final BoneTag tag;

    public DisplayWrapper(T element, AbstractWrapper wrapper, BoneTag tag) {
        this(element, wrapper.node(), wrapper.getDefaultPose(), tag);
    }

    public DisplayWrapper(T element, Node node, Pose defaultPose, BoneTag tag) {
        super(node, defaultPose);
        this.element = element;
        this.tag = tag;
    }

    public T element() {
        return this.element;
    }

    public BoneTag tag() {
        return this.tag;
    }

    public enum BoneTag {
        NONE,
        HEAD,
        HITBOX,
        SEAT,
        DRIVER_SEAT
    }
}
