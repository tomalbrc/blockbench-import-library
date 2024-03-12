package de.tomalbrc.bil.json;

import com.google.gson.*;
import de.tomalbrc.bil.file.bbmodel.BbOutliner;

import java.lang.reflect.Type;
import java.util.UUID;

public class ChildEntryDeserializer implements JsonDeserializer<BbOutliner.ChildEntry> {
    @Override
    public BbOutliner.ChildEntry deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        var entry = new BbOutliner.ChildEntry();
        if (jsonElement.isJsonObject()) {
            entry.outliner = jsonDeserializationContext.deserialize(jsonElement, BbOutliner.class);
        }
        else {
            entry.uuid = jsonDeserializationContext.deserialize(jsonElement, UUID.class);
        }
        return entry;
    }
}