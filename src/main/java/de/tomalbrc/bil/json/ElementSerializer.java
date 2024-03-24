package de.tomalbrc.bil.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.tomalbrc.bil.file.bbmodel.BbElement;
import org.joml.Vector3f;

import java.lang.reflect.Type;
import java.util.List;

public class ElementSerializer implements JsonSerializer<BbElement> {

    String getAxis(Vector3f v) {
        if (v != null) {
            var axis = List.of("x", "y", "z");
            for (int i = 0; i < 3; i++) {
                if (Math.abs(v.get(i)) > 0) {
                    return axis.get(i);
                }
            }
        }

        // default
        return "y";
    }

    float getAngle(Vector3f v) {
        if (v != null) {
            for (int i = 0; i < 3; i++) {
                if (Math.abs(v.get(i)) > 0) {
                    return v.get(i);
                }
            }
        }
        return 0;
    }


    @Override
    public JsonElement serialize(BbElement src, Type typeOfSrc,
                                 JsonSerializationContext context) {

        JsonObject obj = new JsonObject();
        obj.add("from", context.serialize(src.from));
        obj.add("to", context.serialize(src.to));
        obj.add("faces", context.serialize(src.faces));

        if (src.origin.length() > 0 || (src.rotation != null && src.rotation.length() > 0)) {
            JsonObject rot = new JsonObject();
            rot.addProperty("axis", this.getAxis(src.rotation));
            rot.addProperty("angle", this.getAngle(src.rotation));
            rot.add("origin", context.serialize(src.origin));
            obj.add("rotation", rot);
        }

        return obj;
    }
}