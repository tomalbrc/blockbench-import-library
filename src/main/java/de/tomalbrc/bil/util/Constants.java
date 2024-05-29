package de.tomalbrc.bil.util;

import de.tomalbrc.bil.mixin.accessor.LivingEntityAccessor;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;

import java.util.List;

public class Constants {
    public static final EntityDataAccessor<List<ParticleOptions>> DATA_EFFECT_PARTICLES = LivingEntityAccessor.getDATA_EFFECT_PARTICLES();
    public static final int DAMAGE_TINT_COLOR = 0xFF7E7E;
}
