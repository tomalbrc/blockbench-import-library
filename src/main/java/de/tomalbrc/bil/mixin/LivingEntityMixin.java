package de.tomalbrc.bil.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.tomalbrc.bil.api.AnimatedEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @WrapOperation(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isClientSide()Z", ordinal = 1))
    private boolean bil$tickWalkAnimation(Level instance, Operation<Boolean> original) {
        return this instanceof AnimatedEntity;
    }
}
