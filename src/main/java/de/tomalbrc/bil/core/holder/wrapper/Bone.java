package de.tomalbrc.bil.core.holder.wrapper;

import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import eu.pb4.polymer.virtualentity.api.elements.DisplayElement;

public abstract class Bone<T extends DisplayElement> extends DisplayWrapper<T> {
    protected Bone(T element, Node node, Pose defaultPose, boolean isHead) {
        super(element, node, defaultPose, isHead);
    }
}
