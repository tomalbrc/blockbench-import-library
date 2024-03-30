package de.tomalbrc.bil.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.tomalbrc.bil.file.bbmodel.BbFace;

import java.lang.reflect.Type;

public class FaceSerializer implements JsonSerializer<BbFace> {
    @Override
    public JsonElement serialize(BbFace src, Type typeOfSrc,
                                 JsonSerializationContext context) {

        JsonObject obj = new JsonObject();
        obj.addProperty("texture", String.format("#%d", src.texture));
        if (src.cullface != null) obj.addProperty("cullface", src.cullface);
        obj.addProperty("tintindex", 0);
        if (src.rotation != 0) obj.addProperty("rotation", src.rotation);
        obj.add("uv", context.serialize(src.uv));

        return obj;
    }
}