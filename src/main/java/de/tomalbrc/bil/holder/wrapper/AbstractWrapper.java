package de.tomalbrc.bil.holder.wrapper;

import de.tomalbrc.bil.model.Animation;
import de.tomalbrc.bil.model.Node;
import de.tomalbrc.bil.model.Pose;

public abstract class AbstractWrapper {
    private final Node node;
    private final Pose defaultPose;
    protected Animation lastAnimation;
    protected Pose lastPose;

    public AbstractWrapper(Node node, Pose defaultPose) {
        this.node = node;
        this.defaultPose = defaultPose;
        this.lastPose = defaultPose;
    }

    public Node node() {
        return this.node;
    }

    public String name() {
        return this.node.name();
    }

    public Pose getDefaultPose() {
        return this.defaultPose;
    }

    public Animation getLastAnimation() {
        return this.lastAnimation;
    }

    public Pose getLastPose() {
        return this.lastPose;
    }

    public void setLastPose(Pose pose, Animation animation) {
        this.lastAnimation = animation;
        this.lastPose = pose;
    }
}
