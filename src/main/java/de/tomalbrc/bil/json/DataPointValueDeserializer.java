package de.tomalbrc.bil.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import de.tomalbrc.bil.BIL;
import de.tomalbrc.bil.file.bbmodel.Keyframe;
import de.tomalbrc.bil.file.bbmodel.Outliner;
import dev.omega.arcane.ast.MolangExpression;
import dev.omega.arcane.exception.MolangLexException;
import dev.omega.arcane.exception.MolangParseException;
import dev.omega.arcane.parser.MolangParser;

import java.lang.reflect.Type;
import java.util.UUID;

public class DataPointValueDeserializer implements JsonDeserializer<Keyframe.DataPointValue> {
    @Override
    public Keyframe.DataPointValue deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        var entry = new Keyframe.DataPointValue();
        var prim = jsonElement.getAsJsonPrimitive();
        if (prim.isNumber()) {
            entry.value = prim.getAsFloat();
        }
        else if (prim.isString()) {
            entry.expression = prim.getAsString();
        }
        return entry;
    }
}