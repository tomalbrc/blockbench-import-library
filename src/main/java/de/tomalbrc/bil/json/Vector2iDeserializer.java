package de.tomalbrc.bil.json;

import com.google.gson.*;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class Vector2iDeserializer implements JsonDeserializer<Vector2i> {
    @Override
    public Vector2i deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int x = jsonObject.get("width").getAsInt();
        int y = jsonObject.get("height").getAsInt();
        return new Vector2i(x, y);
    }
}
