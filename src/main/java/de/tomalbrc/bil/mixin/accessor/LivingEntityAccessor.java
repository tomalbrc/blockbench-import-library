package de.tomalbrc.bil.mixin.accessor;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    @Accessor
    static EntityDataAccessor<List<ParticleOptions>> getDATA_EFFECT_PARTICLES() {
        throw new UnsupportedOperationException();
    }
}
