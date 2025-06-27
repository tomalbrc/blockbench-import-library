package de.tomalbrc.bil.mixin.accessor;

import eu.pb4.polymer.virtualentity.api.tracker.SimpleDataTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SimpleDataTracker.class, remap = false)
public interface SimpleDataTrackerAccessor {
    @Accessor
    SimpleDataTracker.Entry<?>[] getEntries();
}
