package de.tomalbrc.bil.json;

import com.google.gson.*;
import org.joml.Vector2i;

import java.lang.reflect.Type;

public class Vector2iDeserializer implements JsonDeserializer<Vector2i> {
    @Override
    public Vector2i deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        int x;
        int y;

        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            x = jsonArray.get(0).getAsInt();
            y = jsonArray.get(1).getAsInt();
        } else {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            x = jsonObject.get("width").getAsInt();
            y = jsonObject.get("height").getAsInt();
        }

        return new Vector2i(x, y);
    }
}
