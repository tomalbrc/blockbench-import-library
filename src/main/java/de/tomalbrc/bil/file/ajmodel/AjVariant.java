package de.tomalbrc.bil.file.ajmodel;

import com.google.gson.annotations.SerializedName;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.UUID;

public record AjVariant(UUID uuid,
                        String name,
                        @SerializedName(value = "textureMap", alternate = "texture_map") Object2ObjectOpenHashMap<UUID, UUID> textureMap, // goofy aj model
                        ObjectArrayList<AffectedBoneEntry> affectedBones,
                        boolean affectedBonesIsAWhitelist,
                        @SerializedName("default") boolean isDefault) {

    public record AffectedBoneEntry(String name, UUID value) {
    }
}
