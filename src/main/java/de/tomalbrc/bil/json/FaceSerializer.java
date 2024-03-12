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
        obj.add("uv", context.serialize(src.uv));
        if (src.texture != -1)  obj.addProperty("texture", String.format("#%d", src.texture));
        if (src.cullface != null) obj.addProperty("cullface", src.cullface);
        if (src.tintindex != -1) obj.addProperty("tintindex", src.tintindex);

        return obj;
    }
}