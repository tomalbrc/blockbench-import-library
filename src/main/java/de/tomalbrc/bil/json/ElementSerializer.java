package de.tomalbrc.bil.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.tomalbrc.bil.file.bbmodel.BbElement;
import org.joml.Vector3f;

import java.lang.reflect.Type;
import java.util.Objects;

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

        Vector3f rotation = Objects.requireNonNullElse(src.rotation, new Vector3f());
        Vector3f origin = Objects.requireNonNullElse(src.origin, new Vector3f());

        JsonObject rot = new JsonObject();
        rot.addProperty("x", rotation.x());
        rot.addProperty("y", rotation.y());
        rot.addProperty("z", rotation.z());
        rot.add("origin", context.serialize(origin));
        obj.add("rotation", rot);

        return obj;
    }
}