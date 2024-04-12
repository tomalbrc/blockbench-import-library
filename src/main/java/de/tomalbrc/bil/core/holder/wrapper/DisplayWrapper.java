package de.tomalbrc.bil.core.holder.wrapper;

import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import eu.pb4.polymer.virtualentity.api.elements.DisplayElement;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import eu.pb4.polymer.virtualentity.api.tracker.SimpleDataTracker;

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

    public void startInterpolationIfDirty() {
        SimpleDataTracker simpleDataTracker = (SimpleDataTracker)this.element().getDataTracker();
        if (simpleDataTracker.getEntry(DisplayTrackedData.TRANSLATION).isDirty() ||
                simpleDataTracker.getEntry(DisplayTrackedData.LEFT_ROTATION).isDirty() ||
                simpleDataTracker.getEntry(DisplayTrackedData.SCALE).isDirty() ||
                simpleDataTracker.getEntry(DisplayTrackedData.RIGHT_ROTATION).isDirty())
            this.element().startInterpolation();
    }
}
