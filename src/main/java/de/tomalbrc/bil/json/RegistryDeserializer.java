package de.tomalbrc.bil.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;

public record RegistryDeserializer<T>(Registry<T> registry) implements JsonDeserializer<T> {
    @Override
    public T deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        return this.registry.get(ResourceLocation.parse(element.getAsString())).orElseThrow().value();
    }
}
