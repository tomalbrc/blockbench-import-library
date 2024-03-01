package de.tomalbrc.bil.mixin.accessor;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor
    static EntityDataAccessor<Integer> getDATA_EFFECT_COLOR_ID() {
        throw new UnsupportedOperationException();
    }
}
