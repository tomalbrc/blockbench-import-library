package de.tomalbrc.bil.json;

import com.google.gson.*;
import org.joml.Matrix4f;

import java.lang.reflect.Type;

public class Matrix4fDeserializer implements JsonDeserializer<Matrix4f> {
    @Override
    public Matrix4f deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        float[] values = new float[16];
        for (int i = 0; i < 16; i++) {
            values[i] = jsonArray.get(i).getAsFloat();
        }

        Matrix4f matrix = new Matrix4f();
        matrix.set(values);
        return matrix;
    }
}