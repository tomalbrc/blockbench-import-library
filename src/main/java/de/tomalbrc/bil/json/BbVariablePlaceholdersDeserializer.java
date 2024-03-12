package de.tomalbrc.bil.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import de.tomalbrc.bil.file.extra.BbVariablePlaceholders;

import java.lang.reflect.Type;

public class BbVariablePlaceholdersDeserializer implements JsonDeserializer<BbVariablePlaceholders> {
    @Override
    public BbVariablePlaceholders deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.getAsJsonPrimitive().isString()) {
            return new BbVariablePlaceholders(jsonElement.getAsString());
        }
        return null;
    }
}