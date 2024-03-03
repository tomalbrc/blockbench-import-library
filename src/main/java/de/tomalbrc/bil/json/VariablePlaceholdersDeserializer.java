package de.tomalbrc.bil.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import de.tomalbrc.bil.file.bbmodel.VariablePlaceholders;

import java.lang.reflect.Type;

public class VariablePlaceholdersDeserializer implements JsonDeserializer<VariablePlaceholders> {
    @Override
    public VariablePlaceholders deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.getAsJsonPrimitive().isString()) {
            return new VariablePlaceholders(jsonElement.getAsString());
        }
        return null;
    }
}