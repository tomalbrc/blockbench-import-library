package de.tomalbrc.bil.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * The purpose of this deserializer is to reuse matching UUIDs, so that we can use reference equality.
 */
public class ReferenceUuidDeserializer implements JsonDeserializer<UUID> {
    private static final Object2ObjectOpenHashMap<String, UUID> UUID_CACHE = new Object2ObjectOpenHashMap<>();

    @Override
    public UUID deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        String string = element.getAsString();
        UUID uuid = UUID_CACHE.get(string);
        if (uuid != null) {
            return uuid;
        }

        uuid = UUID.fromString(string);
        UUID_CACHE.put(string, uuid);
        return uuid;
    }
}
