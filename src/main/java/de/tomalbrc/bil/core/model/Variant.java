package de.tomalbrc.bil.core.model;


import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

import java.util.UUID;

public record Variant(
        String name,
        UUID uuid,
        Reference2ObjectOpenHashMap<UUID, PolymerModelData> models,
        ReferenceOpenHashSet<UUID> affectedBones,
        boolean affectedBonesIsAWhitelist
) {
    public boolean isAffected(UUID boneUuid) {
        return this.affectedBonesIsAWhitelist == this.affectedBones.contains(boneUuid);
    }
}

