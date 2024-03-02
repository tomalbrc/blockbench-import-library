package de.tomalbrc.bil.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.tomalbrc.bil.file.bbmodel.Element;

import java.lang.reflect.Type;
import java.util.List;

public class ElementSerializer implements JsonSerializer<Element> {
    @Override
    public JsonElement serialize(Element src, Type typeOfSrc,
                                 JsonSerializationContext context) {

        JsonObject obj = new JsonObject();
        obj.add("from", context.serialize(src.from));
        obj.add("to", context.serialize(src.to));
        obj.add("faces", context.serialize(src.faces));

        if (src.origin.length() > 0) {
            JsonObject rot = new JsonObject();
            rot.addProperty("axis", "y");
            rot.addProperty("angle", 0.f);
            rot.add("origin", context.serialize(src.origin));
            obj.add("rotation", rot);
        }

        return obj;
    }
}