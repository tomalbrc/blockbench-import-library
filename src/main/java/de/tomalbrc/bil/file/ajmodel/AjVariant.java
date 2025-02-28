package de.tomalbrc.bil.file.ajmodel;

import com.google.gson.annotations.SerializedName;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.UUID;

public record AjVariant(UUID uuid,
                        String name,
                        Reference2ObjectOpenHashMap<UUID, UUID> textureMap,
                        ObjectArrayList<AffectedBoneEntry> affectedBones,
                        boolean affectedBonesIsAWhitelist,
                        @SerializedName("default") boolean isDefault) {

    public record AffectedBoneEntry(String name, UUID value) {
    }
}
