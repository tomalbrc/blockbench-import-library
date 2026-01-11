package de.tomalbrc.bil.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.tomalbrc.bil.file.bbmodel.BbElement;

import java.lang.reflect.Type;

public class ElementSerializer implements JsonSerializer<BbElement> {
    @Override
    public JsonElement serialize(BbElement src, Type typeOfSrc,
                                 JsonSerializationContext context) {

        JsonObject obj = new JsonObject();
        obj.add("from", context.serialize(src.from));
        obj.add("to", context.serialize(src.to));
        obj.add("faces", context.serialize(src.faces));
        if (src.lightEmission > 0)
            obj.addProperty("light_emission", src.lightEmission);
        if (src.shade != null && src.shade)
            obj.addProperty("shade", true);

        boolean hasOrigin = src.origin.length() > 0;
        boolean hasRotation = src.rotation != null && src.rotation.length() > 0.0001f;
        if (hasOrigin || hasRotation) {
            JsonObject rot = new JsonObject();
            if (hasRotation) {
                rot.addProperty("x", src.rotation.x());
                rot.addProperty("y", src.rotation.y());
                rot.addProperty("z", src.rotation.z());
            }

            rot.add("origin", context.serialize(src.origin));
            obj.add("rotation", rot);
        }

        return obj;
    }
}