package de.tomalbrc.bil.json;

import com.google.gson.*;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class Vector3fSerializer implements JsonSerializer<Vector3f> {
    @Override
    public JsonElement serialize(Vector3f src, Type typeOfSrc,
                                 JsonSerializationContext context) {

        JsonArray array = new JsonArray();
        array.add(src.x);
        array.add(src.y);
        array.add(src.z);
        return array;
    }
}