package de.tomalbrc.bil.api;

import eu.pb4.polymer.common.impl.tweaker.PacketContext;
import eu.pb4.polymer.core.api.entity.PolymerEntity;
import eu.pb4.polymer.virtualentity.api.data.DisplayEntityData;
import eu.pb4.polymer.virtualentity.api.data.EntityData;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface AnimatedEntity extends PolymerEntity {
    @Nullable
    static AnimatedEntityHolder getHolder(Object obj) {
        return obj instanceof AnimatedEntity animatedEntity ? animatedEntity.getHolder() : null;
    }

    AnimatedEntityHolder getHolder();

    default float getShadowRadius() {
        if (this instanceof Entity entity) {
            return entity.getBbWidth() * 0.6f;
        }
        return 0;
    }

    default int getTeleportDuration() {
        return 3;
    }

    @Override
    default EntityType<?> getPolymerEntityType(PacketContext context) {
        return EntityType.BLOCK_DISPLAY;
    }

    @Override
    default void modifyRawTrackedData(List<SynchedEntityData.DataValue<?>> data, ServerPlayer player, boolean initial) {
        if (this instanceof Entity entity) {
            data.add(SynchedEntityData.DataValue.create(DisplayEntityData.WIDTH, entity.getBbWidth()));
            data.add(SynchedEntityData.DataValue.create(DisplayEntityData.HEIGHT, entity.getBbHeight()));
        }

        data.add(SynchedEntityData.DataValue.create(DisplayEntityData.SHADOW_RADIUS, this.getShadowRadius()));
        data.add(SynchedEntityData.DataValue.create(DisplayEntityData.TELEPORTATION_DURATION, Math.max(0, this.getTeleportDuration())));

        data.add(SynchedEntityData.DataValue.create(EntityData.SILENT, true));
        data.add(SynchedEntityData.DataValue.create(EntityData.NO_GRAVITY, true));
        data.add(SynchedEntityData.DataValue.create(EntityData.NAME_VISIBLE, false));
    }
}
