package de.tomalbrc.bil.json;

import com.google.gson.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.lang.reflect.Type;
import java.util.UUID;

public class JSON {
    public static final GsonBuilder GENERIC_BUILDER = new GsonBuilder()
            // Reference equality
            .registerTypeAdapter(UUID.class, new CachedUuidDeserializer())

            // Custom deserializers
            .registerTypeAdapter(Matrix4f.class, new Matrix4fDeserializer())
            .registerTypeAdapter(Vector3f.class, new Vector3fDeserializer())
            .registerTypeAdapter(Vector3f.class, new Vector3fSerializer())
            .registerTypeAdapter(Vector2i.class, new Vector2iDeserializer())
            .registerTypeAdapter(ResourceLocation.class, new ResourceLocationSerializer())
            .registerTypeAdapter(Item.class, new RegistryDeserializer<>(BuiltInRegistries.ITEM))
            .registerTypeAdapter(SoundEvent.class, new RegistryDeserializer<>(BuiltInRegistries.SOUND_EVENT));

    public static class ResourceLocationSerializer implements JsonDeserializer<ResourceLocation>, JsonSerializer<ResourceLocation> {
        public ResourceLocationSerializer() {
        }

        public ResourceLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return ResourceLocation.parse(GsonHelper.convertToString(json, "location"));
        }

        public JsonElement serialize(ResourceLocation src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }
}
