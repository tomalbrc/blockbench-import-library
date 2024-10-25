package de.tomalbrc.bil.core.model;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record Variant(
        String name,
        UUID uuid,
        Reference2ObjectOpenHashMap<UUID, ResourceLocation> models,
        ReferenceOpenHashSet<UUID> affectedBones,
        boolean affectedBonesIsAWhitelist
) {
    public boolean isAffected(UUID boneUuid) {
        return this.affectedBonesIsAWhitelist == this.affectedBones.contains(boneUuid);
    }
}

