package de.tomalbrc.bil.extra;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.api.AnimatedEntityHolder;
import de.tomalbrc.bil.holder.entity.EntityHolder;
import de.tomalbrc.bil.holder.entity.simple.SimpleEntityHolder;
import de.tomalbrc.bil.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.model.Model;
import de.tomalbrc.bil.model.Pose;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.tracker.InteractionTrackedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ModelEntity extends Interaction implements AnimatedEntity {
    private final EntityHolder<?> holder;

    public ModelEntity(Level level, Model model) {
        super(EntityType.INTERACTION, level);
        this.holder = new SimpleEntityHolder<>(this, model) {
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
            protected void updateCullingBox() {
                // Do nothing, we want to prevent culling.
            }
        };

        EntityAttachment.ofTicking(this.holder, this);
    }

    @Override
    public AnimatedEntityHolder getHolder() {
        return this.holder;
    }

    @Override
    public boolean saveAsPassenger(CompoundTag compoundTag) {
        // Don't save this entity.
        return false;
    }

    @Override
    public EntityType<?> getPolymerEntityType(ServerPlayer player) {
        return EntityType.INTERACTION;
    }

    @Override
    public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        data.add(SynchedEntityData.DataValue.create(InteractionTrackedData.HEIGHT, 2f));
        data.add(SynchedEntityData.DataValue.create(InteractionTrackedData.WIDTH, 2f));
    }
}
