package de.tomalbrc.bil.core.holder.wrapper;

import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import eu.pb4.polymer.virtualentity.api.elements.DisplayElement;

public class DisplayWrapper<T extends DisplayElement> extends AbstractWrapper {
    private final T element;
    private final boolean isHead;

    public DisplayWrapper(T element, AbstractWrapper wrapper, boolean isHead) {
        this(element, wrapper.node(), wrapper.getDefaultPose(), isHead);
    }

    public DisplayWrapper(T element, Node node, Pose defaultPose, boolean isHead) {
        super(node, defaultPose);
        this.element = element;
        this.isHead = isHead;
    }

    public T element() {
        return this.element;
    }

    public boolean isHead() {
        return this.isHead;
    }
}
