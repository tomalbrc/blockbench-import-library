package de.tomalbrc.bil.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import de.tomalbrc.bil.file.bbmodel.BbKeyframe;

import java.lang.reflect.Type;

public class DataPointValueDeserializer implements JsonDeserializer<BbKeyframe.DataPointValue> {
    @Override
    public BbKeyframe.DataPointValue deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        var entry = new BbKeyframe.DataPointValue();
        var prim = jsonElement.getAsJsonPrimitive();
        if (prim.isNumber()) {
            entry.setValue(prim.getAsFloat());
        } else if (prim.isString()) {
            entry.setStringValue(prim.getAsString());
        }
        return entry;
    }
}