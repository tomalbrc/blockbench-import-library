package de.tomalbrc.bil.core.holder.entity.living;

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
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
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
        this.hitboxInteraction.setSendPositionUpdates(false);
        this.addElement(this.hitboxInteraction);

        this.collisionElement = CollisionElement.createWithRedirect(parent);
        this.collisionElement.setSendPositionUpdates(false);
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
        display.element().setYaw(this.parent.yBodyRot);
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
    protected void applyPose(Pose pose, DisplayWrapper<?> display) {
        Vector3f translation = pose.translation();
        boolean isHead = display.isHead();
        boolean isDead = this.parent.deathTime > 0;

        if (isHead || isDead) {
            Quaternionf bodyRotation = new Quaternionf();
            if (isDead) {
                bodyRotation.rotateZ(-this.deathAngle * Mth.HALF_PI);
                translation.rotate(bodyRotation);
            }

            if (isHead) {
                bodyRotation.rotateY(Mth.DEG_TO_RAD * -Mth.rotLerp(0.5f, this.parent.yHeadRotO - this.parent.yBodyRotO, this.parent.yHeadRot - this.parent.yBodyRot));
                bodyRotation.rotateX(Mth.DEG_TO_RAD * Mth.lerp(0.5f, this.parent.xRotO, this.parent.getXRot()));
            }

            display.element().setLeftRotation(bodyRotation.mul(pose.readOnlyLeftRotation()));
        } else {
            display.element().setLeftRotation(pose.readOnlyLeftRotation());
        }

        if (this.entityScale != 1F) {
            translation.mul(this.entityScale);
            display.element().setScale(pose.scale().mul(this.entityScale));
        } else {
            display.element().setScale(pose.readOnlyScale());
        }

        display.element().setTranslation(translation.sub(0, this.dimensions.height() - 0.01f, 0));
        display.element().setRightRotation(pose.readOnlyRightRotation());

        display.element().startInterpolationIfDirty();
    }

    @Override
    protected void startWatchingExtraPackets(ServerGamePacketListenerImpl player, Consumer<Packet<ClientGamePacketListener>> consumer) {
        super.startWatchingExtraPackets(player, consumer);

        for (var packet : Utils.updateClientInteraction(this.hitboxInteraction, this.dimensions)) {
            consumer.accept((Packet<ClientGamePacketListener>) packet);
        }

        if (this.parent.canBreatheUnderwater()) {
            consumer.accept(new ClientboundUpdateMobEffectPacket(this.collisionElement.getEntityId(), new MobEffectInstance(MobEffects.WATER_BREATHING, -1, 0, false, false), false));
        }

        if (this.parent instanceof Leashable leashable && leashable.getLeashData() != null && leashable.getLeashHolder() != null) {
            consumer.accept(new ClientboundSetEntityLinkPacket(this.parent, leashable.getLeashHolder()));
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
        float width = scale * (this.dimensions.width() * 2);
        float height = -this.dimensions.height() - 1;

        for (Bone bone : this.bones) {
            bone.element().setDisplaySize(width, height);
        }
    }

    @Override
    public void onDimensionsUpdated(EntityDimensions dimensions) {
        this.updateEntityScale(this.scale);
        super.onDimensionsUpdated(dimensions);

        this.collisionElement.setSize(Math.max(1, Utils.toSlimeSize(Math.min(dimensions.width(), dimensions.height()))));
        this.sendPacket(new ClientboundBundlePacket(Utils.updateClientInteraction(this.hitboxInteraction, dimensions)));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key, Object object) {
        super.onSyncedDataUpdated(key, object);
        if (key.equals(Constants.DATA_EFFECT_PARTICLES)) {
            this.collisionElement.getDataTracker().set(Constants.DATA_EFFECT_PARTICLES, (List<ParticleOptions>) object);
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
