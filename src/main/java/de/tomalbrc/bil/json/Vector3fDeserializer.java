package de.tomalbrc.bil.json;

import com.google.gson.*;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class Vector3fDeserializer implements JsonDeserializer<Vector3f> {
    @Override
    public Vector3f deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        float x = jsonArray.get(0).getAsFloat();
        float y = jsonArray.get(1).getAsFloat();
        float z = jsonArray.get(2).getAsFloat();
        return new Vector3f(x, y, z);
    }
}