package de.tomalbrc.bil.core.extra;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.api.AnimatedEntityHolder;
import de.tomalbrc.bil.core.holder.entity.EntityHolder;
import de.tomalbrc.bil.core.holder.entity.simple.SimpleEntityHolder;
import de.tomalbrc.bil.core.holder.wrapper.DisplayWrapper;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.core.model.Pose;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.tracker.InteractionTrackedData;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class ModelEntity extends Interaction implements AnimatedEntity {
    private final EntityHolder<?> holder;

    public ModelEntity(Level level, Model model) {
        super(EntityType.INTERACTION, level);
        this.holder = new SimpleEntityHolder<>(this, model) {
            @Override
            public void updateElement(ServerPlayer serverPlayer, DisplayWrapper<?> display, @Nullable Pose pose) {
                display.element().setYaw(this.parent.getYRot());
                display.element().setPitch(this.parent.getXRot());
                super.updateElement(serverPlayer, display, pose);
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
    public boolean saveAsPassenger(ValueOutput valueOutput) {
        // Don't save this entity.
        return false;
    }

    @Override
    public EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.INTERACTION;
    }

    @Override
    public void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        data.add(SynchedEntityData.DataValue.create(InteractionTrackedData.HEIGHT, 2f));
        data.add(SynchedEntityData.DataValue.create(InteractionTrackedData.WIDTH, 2f));
    }
}
