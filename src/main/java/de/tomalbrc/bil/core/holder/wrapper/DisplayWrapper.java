package de.tomalbrc.bil.core.holder.wrapper;

import de.tomalbrc.bil.core.element.PerPlayerTransformableElement;
import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;

public class DisplayWrapper<T extends PerPlayerTransformableElement> extends AbstractWrapper {
    private final T element;

    public DisplayWrapper(T element, AbstractWrapper wrapper) {
        this(element, wrapper.node(), wrapper.getDefaultPose());
    }

    public DisplayWrapper(T element, Node node, Pose defaultPose) {
        super(node, defaultPose);
        this.element = element;
    }

    public T element() {
        return this.element;
    }

}
