package de.tomalbrc.bil.json;

import com.google.gson.*;
import de.tomalbrc.bil.file.bbmodel.Outliner;
import org.joml.Vector3f;

import java.lang.reflect.Type;
import java.util.UUID;

public class ChildEntryDeserializer implements JsonDeserializer<Outliner.ChildEntry> {
    @Override
    public Outliner.ChildEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        var entry = new Outliner.ChildEntry();
        if (jsonElement.isJsonObject()) {
            entry.outliner = jsonDeserializationContext.deserialize(jsonElement, Outliner.class);
        }
        else {
            entry.uuid = jsonDeserializationContext.deserialize(jsonElement, UUID.class);
        }
        return entry;
    }
}