package de.tomalbrc.bil.file.ajmodel;

import com.google.gson.annotations.SerializedName;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.UUID;

public record AjVariant(UUID uuid,
                        String name,
                        Object2ObjectOpenHashMap<UUID, UUID> textureMap, // goofy aj model
                        Object2ObjectOpenHashMap<UUID, UUID> texture_map, // goofy aj blueprint
                        ObjectArrayList<AffectedBoneEntry> affectedBones,
                        boolean affectedBonesIsAWhitelist,
                        @SerializedName("default") boolean isDefault) {

    public record AffectedBoneEntry(String name, UUID value) {
    }

    @Override
    public Object2ObjectOpenHashMap<UUID, UUID> textureMap() {
        return textureMap == null ? texture_map : textureMap;
    }
}
