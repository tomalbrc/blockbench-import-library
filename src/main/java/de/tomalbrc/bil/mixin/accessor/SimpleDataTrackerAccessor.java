package de.tomalbrc.bil.mixin.accessor;

import eu.pb4.polymer.virtualentity.api.data.SimpleSynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SimpleSynchedEntityData.class, remap = false)
public interface SimpleDataTrackerAccessor {
    @Accessor
    SimpleSynchedEntityData.Entry<?>[] getEntries();
}
