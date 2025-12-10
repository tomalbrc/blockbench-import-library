package de.tomalbrc.bil.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public record RegistryDeserializer<T>(Registry<@NotNull T> registry) implements JsonDeserializer<T> {
    @Override
    public T deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        return this.registry.get(Identifier.parse(element.getAsString())).orElseThrow().value();
    }
}
