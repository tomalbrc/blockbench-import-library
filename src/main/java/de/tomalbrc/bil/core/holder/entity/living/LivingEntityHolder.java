package de.tomalbrc.bil.core.holder.entity.living;

import com.mojang.math.Axis;
import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.element.CollisionElement;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.holder.wrapper.Bone;
import de.tomalbrc.bil.core.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.core.holder.wrapper.Locator;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Pose;
import de.tomalbrc.bil.util.Constants;
import de.tomalbrc.bil.util.Utils;
import eu.pb4.polymer.virtualentity.api.elements.InteractionElement;
import eu.pb4.polymer.virtualentity.api.tracker.EntityTrackedData;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.function.Consumer;

public class LivingEntityHolder<T extends LivingEntity & AnimatedEntity> extends EntityHolder<T> {
    protected final InteractionElement hitboxInteraction;
    protected final CollisionElement collisionElement;
    protected float deathAngle;
    protected float entityScale = 1F;

    public LivingEntityHolder(T parent, Model model) {
        super(parent, model);

        this.hitboxInteraction = InteractionElement.redirect(parent);
        this.addElement(this.hitboxInteraction);

        this.collisionElement = CollisionElement.createWithRedirect(parent);
        this.addElement(this.collisionElement);
    }

    @Override
    protected void onAsyncTick() {
        if (this.parent.deathTime > 0) {
            this.deathAngle = Math.min((float) Math.sqrt((this.parent.deathTime) / 20.0F * 1.6F), 1.f);
        }

        super.onAsyncTick();
    }

    @Override
    public void updateElement(DisplayWrapper<?> display, @Nullable Pose pose) {
        if (pose == null) {
            this.applyPose(display.getLastPose(), display);
        } else {
            this.applyPose(pose, display);
        }
    }

    @Override
    protected void updateLocator(Locator locator) {
        if (locator.requiresUpdate()) {
            Pose pose = this.animationComponent.findPose(locator);
            if (pose == null) {
                locator.updateListeners(this, locator.getLastPose());
            } else {
                locator.updateListeners(this, pose);
            }
        }
    }

    @Override
    public void applyPose(Pose pose, DisplayWrapper<?> display) {
        Quaternionf bodyRotation = Axis.YP.rotationDegrees(-Mth.rotLerp(1.f, this.parent.yBodyRotO, this.parent.yBodyRot));
        if (this.parent.deathTime > 0) {
            bodyRotation.mul(Axis.ZP.rotation(-this.deathAngle * Mth.HALF_PI));
        }

        Vector3f scale = pose.scale();
        Vector3f translation = pose.translation().rotate(bodyRotation);
        if (this.scale != 1.0f) {
            translation.mul(this.scale);
            scale.mul(this.scale);
        }
        translation.add(0, -this.dimensions.height*0.75f + 0.01f, 0);

        if (display.isHead()) {
            bodyRotation.mul(Axis.YP.rotation((float) -Math.toRadians(Mth.rotLerp(0.5f, this.parent.yHeadRotO - this.parent.yBodyRotO, this.parent.yHeadRot - this.parent.yBodyRot))));
            bodyRotation.mul(Axis.XP.rotation((float) Math.toRadians(Mth.rotLerp(0.5f, this.parent.getXRot(), this.parent.xRotO))));
        }

        if (this.entityScale != 1.f) {
            translation.mul(this.entityScale);
            display.element().setScale(scale.mul(this.entityScale));
        } else {
            display.element().setScale(scale);
        }

        // Update data tracker values
        display.element().setTranslation(translation.mul(this.entityScale));
        display.element().setRightRotation(bodyRotation.mul(pose.leftRotation()));
        display.element().setLeftRotation(pose.rightRotation().mul(Axis.YP.rotationDegrees(0.f)));

        display.startInterpolationIfDirty();
    }

    @Override
    protected void startWatchingExtraPackets(ServerGamePacketListenerImpl player, Consumer<Packet<ClientGamePacketListener>> consumer) {
        super.startWatchingExtraPackets(player, consumer);

        for (var packet : Utils.updateClientInteraction(this.hitboxInteraction, this.dimensions)) {
            consumer.accept(packet);
        }

        if (this.parent.canBreatheUnderwater()) {
            consumer.accept(new ClientboundUpdateMobEffectPacket(this.collisionElement.getEntityId(), new MobEffectInstance(MobEffects.WATER_BREATHING, -1, 0, false, false)));
        }

        consumer.accept(new ClientboundSetPassengersPacket(this.parent));
    }

    @Override
    protected void addDirectPassengers(IntList passengers) {
        super.addDirectPassengers(passengers);
        passengers.add(this.hitboxInteraction.getEntityId());
        passengers.add(this.collisionElement.getEntityId());
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

    @Override
    public int getLeashedId() {
        return this.collisionElement.getEntityId();
    }

    @Override
    public int getEntityEventId() {
        return this.collisionElement.getEntityId();
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
        this.updateEntityScale(this.scale);
        super.onDimensionsUpdated(dimensions);

        this.collisionElement.setSize(Utils.toSlimeSize(Math.min(dimensions.width, dimensions.height)));
        this.sendPacket(new ClientboundBundlePacket(Utils.updateClientInteraction(this.hitboxInteraction, dimensions)));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key, Object object) {
        super.onSyncedDataUpdated(key, object);
        if (key.equals(Constants.DATA_EFFECT_COLOR)) {
            this.collisionElement.getDataTracker().set(Constants.DATA_EFFECT_COLOR, (int) object);
        }

        if (key.equals(EntityTrackedData.NAME_VISIBLE)) {
            this.hitboxInteraction.setCustomNameVisible((boolean) object);
        }

        if (key.equals(EntityTrackedData.CUSTOM_NAME)) {
            // noinspection unchecked
            this.hitboxInteraction.getDataTracker().set(EntityTrackedData.CUSTOM_NAME, (Optional<Component>) object);
        }
    }

    @Override
    protected void updateOnFire(boolean displayFire) {
        this.hitboxInteraction.setOnFire(displayFire);
        super.updateOnFire(displayFire);
    }

    @Override
    protected void updateInvisibility(boolean isInvisible) {
        this.hitboxInteraction.setInvisible(isInvisible);
        super.updateInvisibility(isInvisible);
    }

    @Override
    public float getScale() {
        return this.entityScale;
    }

    @Override
    public void setScale(float scale) {
        this.updateEntityScale(scale);
        super.setScale(scale);
    }

    protected void updateEntityScale(float scalar) {
        this.entityScale = this.parent.getScale() * scalar;
    }
}
