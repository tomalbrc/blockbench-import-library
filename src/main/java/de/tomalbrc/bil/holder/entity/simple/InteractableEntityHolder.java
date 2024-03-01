package de.tomalbrc.bil.holder.entity.simple;

import de.tomalbrc.bil.api.AjEntity;
import de.tomalbrc.bil.holder.entity.EntityHolder;
import de.tomalbrc.bil.holder.wrapper.Bone;
import de.tomalbrc.bil.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.model.Model;
import de.tomalbrc.bil.model.Pose;
import de.tomalbrc.bil.util.Utils;
import eu.pb4.polymer.virtualentity.api.elements.InteractionElement;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class InteractableEntityHolder<T extends Entity & AjEntity> extends EntityHolder<T> {
    protected final InteractionElement hitboxInteraction;

    public InteractableEntityHolder(T parent, Model model) {
        super(parent, model);

        this.hitboxInteraction = InteractionElement.redirect(parent);
        this.addElement(this.hitboxInteraction);
    }

    @Override
    protected void startWatchingExtraPackets(ServerGamePacketListenerImpl player, Consumer<Packet<ClientGamePacketListener>> consumer) {
        super.startWatchingExtraPackets(player, consumer);

        for (var packet : Utils.updateClientInteraction(this.hitboxInteraction, this.dimensions)) {
            consumer.accept(packet);
        }

        consumer.accept(new ClientboundSetPassengersPacket(this.parent));
    }

    @Override
    public void updateElement(DisplayWrapper<?> display, @Nullable Pose pose) {
        display.element().setYaw(this.parent.getYRot());
        display.element().setPitch(this.parent.getXRot());
        if (pose == null) {
            this.applyPose(display.getLastPose(), display);
        } else {
            this.applyPose(pose, display);
        }
    }

    @Override
    protected void applyPose(Pose pose, DisplayWrapper<?> display) {
        Vector3f translation = pose.translation();
        if (this.scale != 1F) {
            translation.mul(this.scale);
            display.setScale(pose.scale().mul(this.scale));
        } else {
            display.setScale(pose.readOnlyScale());
        }

        display.setTranslation(translation.sub(0, this.dimensions.height - 0.01f, 0));
        display.setLeftRotation(pose.leftRotation());
        display.setRightRotation(pose.rightRotation());

        display.startInterpolation();
    }

    @Override
    protected void addDirectPassengers(IntList passengers) {
        super.addDirectPassengers(passengers);
        passengers.add(this.hitboxInteraction.getEntityId());
    }

    @Override
    protected void updateCullingBox() {
        float scale = this.getScale();
        float width = scale * (this.dimensions.width * 2);
        float height = -this.dimensions.height - 1;

        for (Bone bone : this.bones) {
            bone.element().setDisplaySize(width, height);
        }
    }

    @Override
    public void onDimensionsUpdated(EntityDimensions dimensions) {
        super.onDimensionsUpdated(dimensions);
        this.sendPacket(new ClientboundBundlePacket(Utils.updateClientInteraction(this.hitboxInteraction, dimensions)));
    }

    @Override
    public int getDisplayVehicleId() {
        return this.hitboxInteraction.getEntityId();
    }

    @Override
    public int getVehicleId() {
        return this.hitboxInteraction.getEntityId();
    }

    @Override
    public int getCritParticleId() {
        return this.hitboxInteraction.getEntityId();
    }
}
