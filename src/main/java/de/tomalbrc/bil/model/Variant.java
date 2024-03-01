package de.tomalbrc.bil.model;


import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public record Variant(
        String name,
        UUID uuid,
        Object2ObjectOpenHashMap<UUID, ModelInfo> models,
        ReferenceOpenHashSet<UUID> affectedBones,
        boolean affectedBonesIsAWhitelist
) {

    public boolean isAffected(UUID boneUuid) {
        return this.affectedBonesIsAWhitelist == this.affectedBones.contains(boneUuid);
    }

    public record ModelInfo(
            int customModelData,
            ResourceLocation resourceLocation
    ) {
    }
}

