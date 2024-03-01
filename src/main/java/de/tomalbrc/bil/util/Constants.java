package de.tomalbrc.bil.util;

import de.tomalbrc.bil.mixin.accessor.LivingEntityAccessor;
import net.minecraft.network.syncher.EntityDataAccessor;

public class Constants {
    public static final EntityDataAccessor<Integer> DATA_EFFECT_COLOR = LivingEntityAccessor.getDATA_EFFECT_COLOR_ID();
    public static final int DAMAGE_TINT_COLOR = 0xFF7E7E;
}
