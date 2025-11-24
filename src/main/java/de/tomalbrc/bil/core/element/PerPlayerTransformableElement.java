package de.tomalbrc.bil.core.element;

import com.mojang.math.MatrixUtil;
import com.mojang.math.Transformation;
import de.tomalbrc.bil.mixin.accessor.SimpleDataTrackerAccessor;
import de.tomalbrc.bil.util.Utils;
import eu.pb4.polymer.virtualentity.api.tracker.DataTrackerLike;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import eu.pb4.polymer.virtualentity.api.tracker.SimpleDataTracker;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.*;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public interface PerPlayerTransformableElement extends PolymerDisplayElementLike {
    Map<ServerPlayer, Data> playerDataTrackers();

    default void addDataTracker(ServerPlayer serverPlayer) {
        playerDataTrackers().get(serverPlayer).setDataTracker(copyEntries((SimpleDataTracker) getDataTracker(), (SimpleDataTracker) createDataTracker()));
    }

    default void resetDataTracker(ServerPlayer serverPlayer) {
        var map = this.playerDataTrackers().get(serverPlayer);
        if (map != null)
            map.setDataTracker(null);
    }

    default DataTrackerLike dataTrackerOrDefault(ServerPlayer serverPlayer) {
        if (serverPlayer == null) {
            return getDataTracker();
        }

        var playerData = this.playerDataTrackers().get(serverPlayer);
        if (playerData == null || playerData.getDataTracker() == null) {
            return getDataTracker();
        }
        return playerData.getDataTracker();
    }

    default void setTransformation(ServerPlayer serverPlayer, Transformation transformation) {
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.TRANSLATION, transformation.getTranslation());
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.LEFT_ROTATION, transformation.getLeftRotation());
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.SCALE, transformation.getScale());
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.RIGHT_ROTATION, transformation.getRightRotation());
    }

    default void setTransformation(ServerPlayer serverPlayer, Matrix4f matrix) {
        float f = 1.0F / matrix.m33();
        Triple<Quaternionf, Vector3f, Quaternionf> triple = MatrixUtil.svdDecompose((new Matrix3f(matrix)).scale(f));
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.TRANSLATION, matrix.getTranslation(new Vector3f()));
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.LEFT_ROTATION, new Quaternionf(triple.getLeft()));
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.SCALE, new Vector3f(triple.getMiddle()));
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.RIGHT_ROTATION, new Quaternionf(triple.getRight()));
    }

    default void setTransformation(ServerPlayer serverPlayer, Matrix4x3f matrix) {
        Triple<Quaternionf, Vector3f, Quaternionf> triple = MatrixUtil.svdDecompose((new Matrix3f()).set(matrix));
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.TRANSLATION, matrix.getTranslation(new Vector3f()));
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.LEFT_ROTATION, new Quaternionf(triple.getLeft()));
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.SCALE, new Vector3f(triple.getMiddle()));
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.RIGHT_ROTATION, new Quaternionf(triple.getRight()));
    }

    default boolean isTransformationDirty(ServerPlayer serverPlayer) {
        var dataTracker = dataTrackerOrDefault(serverPlayer);
        return dataTracker.isDirty(DisplayTrackedData.TRANSLATION) || dataTracker.isDirty(DisplayTrackedData.LEFT_ROTATION) || dataTracker.isDirty(DisplayTrackedData.SCALE) || dataTracker.isDirty(DisplayTrackedData.RIGHT_ROTATION);
    }

    default void setTranslation(ServerPlayer serverPlayer, Vector3fc vector3f) {
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.TRANSLATION, new Vector3f(vector3f));
    }

    default Vector3fc getTranslation(ServerPlayer serverPlayer) {
        return this.dataTrackerOrDefault(serverPlayer).get(DisplayTrackedData.TRANSLATION);
    }

    default void setScale(ServerPlayer serverPlayer, Vector3fc vector3f) {
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.SCALE, new Vector3f(vector3f));
    }

    default Vector3fc getScale(ServerPlayer serverPlayer) {
        return this.dataTrackerOrDefault(serverPlayer).get(DisplayTrackedData.SCALE);
    }

    default void setLeftRotation(ServerPlayer serverPlayer, Quaternionfc quaternion) {
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.LEFT_ROTATION, new Quaternionf(quaternion));
    }

    default Quaternionfc getLeftRotation(ServerPlayer serverPlayer) {
        return this.dataTrackerOrDefault(serverPlayer).get(DisplayTrackedData.LEFT_ROTATION);
    }

    default void setRightRotation(ServerPlayer serverPlayer, Quaternionfc quaternion) {
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.RIGHT_ROTATION, new Quaternionf(quaternion));
    }

    default Quaternionfc getRightRotation(ServerPlayer serverPlayer) {
        return this.dataTrackerOrDefault(serverPlayer).get(DisplayTrackedData.RIGHT_ROTATION);
    }

    default Integer getInterpolationDuration(ServerPlayer serverPlayer) {
        return this.dataTrackerOrDefault(serverPlayer).get(DisplayTrackedData.INTERPOLATION_DURATION);
    }

    default void setInterpolationDuration(ServerPlayer serverPlayer, int interpolationDuration) {
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.INTERPOLATION_DURATION, interpolationDuration);
    }

    default Integer getTeleportDuration(ServerPlayer serverPlayer) {
        return this.dataTrackerOrDefault(serverPlayer).get(DisplayTrackedData.TELEPORTATION_DURATION);
    }

    default void setTeleportDuration(ServerPlayer serverPlayer, int interpolationDuration) {
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.TELEPORTATION_DURATION, interpolationDuration);
    }

    default Integer getStartInterpolation(ServerPlayer serverPlayer) {
        return this.dataTrackerOrDefault(serverPlayer).get(DisplayTrackedData.START_INTERPOLATION);
    }

    default void startInterpolation(ServerPlayer serverPlayer) {
        this.dataTrackerOrDefault(serverPlayer).setDirty(DisplayTrackedData.START_INTERPOLATION, true);
    }

    default void setStartInterpolation(ServerPlayer serverPlayer, int startInterpolation) {
        this.dataTrackerOrDefault(serverPlayer).set(DisplayTrackedData.START_INTERPOLATION, startInterpolation, true);
    }

    default void startInterpolationIfDirty(ServerPlayer serverPlayer) {
        if (this.isTransformationDirty(serverPlayer)) {
            this.startInterpolation(serverPlayer);
        }
    }

    default void sendTrackerIfDirty(ServerGamePacketListenerImpl serverPlayer, List<SynchedEntityData.DataValue<?>> list) {
        if (list != null && serverPlayer != null) {
            serverPlayer.send(new ClientboundSetEntityDataPacket(getEntityId(), list));
        }
    }

    default void sendTrackerUpdatesPerPlayer() {
        var holder = getHolder();
        if (holder == null)
            return;

        var defaultDirty = this.getDataTracker().getDirtyEntries();
        var arr = holder.getWatchingPlayers().toArray(Utils.EMPTY_CONNECTION_ARRAY);
        for (ServerGamePacketListenerImpl watchingPlayer : arr) {
            if (watchingPlayer != null) {
                var data = this.playerDataTrackers().get(watchingPlayer.player);
                if (data != null && data.dataTracker != null) {
                    sendTrackerIfDirty(watchingPlayer, data.dataTracker.getDirtyEntries());
                } else {
                    sendTrackerIfDirty(watchingPlayer, defaultDirty);
                }
            }
        }
    }

    class Data {
        SimpleDataTracker dataTracker;
        float yRot;
        float xRot;
        Vec3 pos;

        public Data() {}

        public Data(float yRot, float xRot, Vec3 pos) {
            this.dataTracker = null;
            this.yRot = yRot;
            this.xRot = xRot;
            this.pos = pos;
        }


        public Vec3 getPos() {
            return pos;
        }

        public float getxRot() {
            return xRot;
        }

        public float getyRot() {
            return yRot;
        }

        public SimpleDataTracker getDataTracker() {
            return dataTracker;
        }

        public Vec3 pos() {
            return pos;
        }

        public void setDataTracker(SimpleDataTracker dataTracker) {
            this.dataTracker = dataTracker;
        }

        public void setYaw(float yRot) {
            this.yRot = yRot;
        }

        public void setPitch(float xRot) {
            this.xRot = xRot;
        }

        public void setPos(Vec3 pos) {
            this.pos = pos;
        }
    }

    static SimpleDataTracker copyEntries(SimpleDataTracker from, SimpleDataTracker to) {
        for (SimpleDataTracker.Entry entry : ((SimpleDataTrackerAccessor)from).getEntries()) {
            if (!entry.isUnchanged())
                to.set(entry.getData(), entry.get());
        }
        return to;
    }
}
