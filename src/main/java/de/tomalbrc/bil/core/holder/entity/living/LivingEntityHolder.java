package de.tomalbrc.bil.core.holder.entity.living;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.core.element.CollisionElement;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Node;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

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
    public void updateElement(ServerPlayer serverPlayer, DisplayWrapper<?> displayWrapper, @Nullable Pose pose) {
        displayWrapper.element().setYaw(this.parent.yBodyRot);
        super.updateElement(serverPlayer, displayWrapper, pose);
    }

    protected Vector3fc getModelSpaceOrigin(ServerPlayer player, Node node) {
        var bone = this.getBone(node);
        return bone == null ? Utils.ZERO_VEC3F : bone.getLastPose(player).translation();
    }

    @Nullable
    public static Node findHeadNode(Node node) {
        Node res = null;
        Node current = node;
        while (current != null) {
            if (current.tag() == Node.NodeTag.HEAD) {
                res = current;
            }
            current = current.parent();
        }
        return res;
    }

    @Override
    protected void applyPose(ServerPlayer serverPlayer, Pose pose, DisplayWrapper<?> displayWrapper) {
        Vector3f translation = new Vector3f(pose.translation());
        Quaternionf leftRotation = new Quaternionf(pose.readOnlyLeftRotation());

        Node node = displayWrapper.node();
        boolean isHead = node.tag() == Node.NodeTag.HEAD;
        boolean isHeadChild = node.tag() == Node.NodeTag.HEAD_CHILD;
        boolean isDead = this.parent.deathTime > 0;

        if (!isDead && (isHead || isHeadChild)) {
            Node headNode = isHead ? node : findHeadNode(node);

            if (headNode != null) {
                Vector3fc pivot = getModelSpaceOrigin(serverPlayer, headNode);

                float yawDiff = this.parent.yHeadRot - this.parent.yBodyRot;
                float yawDiffO = this.parent.yHeadRotO - this.parent.yBodyRotO;
                float netYaw = Mth.rotLerp(0.5f, yawDiffO, yawDiff);
                float netPitch = Mth.lerp(0.5f, this.parent.xRotO, this.parent.getXRot());

                Quaternionf lookRotation = new Quaternionf()
                        .rotateY(Mth.DEG_TO_RAD * -netYaw)
                        .rotateX(Mth.DEG_TO_RAD * netPitch);

                lookRotation.mul(leftRotation, leftRotation);

                translation.sub(pivot).rotate(lookRotation).add(pivot);
            }
        }

        if (isDead) {
            Quaternionf deathRotation = new Quaternionf();
            deathRotation.rotateZ(-this.deathAngle * Mth.HALF_PI);
            translation.rotate(deathRotation);
            deathRotation.mul(leftRotation, leftRotation);
        }

        if (this.entityScale != 1F) {
            translation.mul(this.entityScale);
            displayWrapper.element().setScale(serverPlayer, pose.scale().mul(this.entityScale));
        } else {
            displayWrapper.element().setScale(serverPlayer, pose.readOnlyScale());
        }

        displayWrapper.element().setLeftRotation(serverPlayer, leftRotation);
        displayWrapper.element().setTranslation(serverPlayer, translation.sub(0, this.dimensions.height() - 0.01f, 0));
        displayWrapper.element().setRightRotation(serverPlayer, pose.readOnlyRightRotation());
        displayWrapper.element().startInterpolationIfDirty(serverPlayer);
    }

    @Override
    protected void startWatchingExtraPackets(ServerGamePacketListenerImpl player, Consumer<Packet<@NotNull ClientGamePacketListener>> consumer) {
        super.startWatchingExtraPackets(player, consumer);

        for (var packet : Utils.updateClientInteraction(this.hitboxInteraction, this.dimensions)) {
            // noinspection unchecked
            consumer.accept((Packet<@NotNull ClientGamePacketListener>) packet);
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

        for (int i = 0; i < this.bones.length; i++) {
            this.bones[i].element().setDisplaySize(width, height);
        }
    }

    @Override
    public void onDimensionsUpdated(EntityDimensions dimensions) {
        this.updateEntityScale(this.scale);
        super.onDimensionsUpdated(dimensions);

        var size = Utils.toSlimeSize(Math.min(dimensions.width(), dimensions.height()));
        if (size <= 0) {
            var attributeInstance = new AttributeInstance(Attributes.SCALE, (instance) -> {});
            attributeInstance.setBaseValue(0.01);
            var attributesPacket = new ClientboundUpdateAttributesPacket(this.collisionElement.getEntityId(), List.of(attributeInstance));
            this.sendPacket(attributesPacket);
            size = 1;
        }

        this.collisionElement.setSize(size);
        this.sendPacket(new ClientboundBundlePacket(Utils.updateClientInteraction(this.hitboxInteraction, dimensions)));
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key, Object object) {
        super.onSyncedDataUpdated(key, object);
        if (key.equals(Constants.DATA_EFFECT_PARTICLES)) {
            // noinspection unchecked
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

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
    }
}
