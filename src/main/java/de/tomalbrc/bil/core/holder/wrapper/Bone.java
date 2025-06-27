package de.tomalbrc.bil.core.holder.wrapper;

import de.tomalbrc.bil.core.element.PerPlayerTransformableElement;
import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;

public abstract class Bone<T extends PerPlayerTransformableElement> extends DisplayWrapper<T> {
    protected Bone(T element, Node node, Pose defaultPose, boolean isHead) {
        super(element, node, defaultPose, isHead);
    }
}
