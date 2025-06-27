package de.tomalbrc.bil.core.holder.wrapper;

import de.tomalbrc.bil.core.model.Animation;
import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public abstract class AbstractWrapper {
    private final Node node;
    private final Pose defaultPose;
    protected Animation lastAnimation;
    protected Pose lastPose;

    protected Map<ServerPlayer, Animation> lastAnimationPerPlayer = new Object2ReferenceOpenHashMap<>();
    protected Map<ServerPlayer, Pose> lastPosePerPlayer = new Object2ReferenceOpenHashMap<>();

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

    public Animation getLastAnimation(ServerPlayer serverPlayer) {
        return this.lastAnimationPerPlayer.getOrDefault(serverPlayer, this.lastAnimation);
    }

    public Pose getLastPose(ServerPlayer serverPlayer) {
        return this.lastPosePerPlayer.getOrDefault(serverPlayer, this.lastPose);
    }

    public void setLastPose(ServerPlayer serverPlayer, Pose pose, Animation animation) {
        if (serverPlayer != null) {
            this.lastAnimationPerPlayer.put(serverPlayer, animation);
            this.lastPosePerPlayer.put(serverPlayer, pose);
        } else {
            this.lastAnimation = animation;
            this.lastPose = pose;
        }
    }

    public void resetLastPose(ServerPlayer serverPlayer) {
        this.lastAnimationPerPlayer.remove(serverPlayer);
        this.lastPosePerPlayer.remove(serverPlayer);
    }
}
