package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import de.tomalbrc.bil.json.CachedUuidDeserializer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

public class BbFace {
    public List<Float> uv;
    public TextureId texture;
    public int tintindex = 0;

    public int rotation;
    public String cullface;

    public static class TextureId { // compat for old and new ajblueprint format
        Integer id;
        UUID uuid;

        public TextureId(Integer id) {
            this.id = id;
        }

        public TextureId(UUID uuid) {
            this.uuid = uuid;
        }

        public boolean matches(BbTexture texture) {
            if (this.id != null) return texture.id == id;
            if (this.uuid != null) return texture.uuid == uuid;

            return false;
        }

        @Override
        public String toString() {
            return id != null ? id.toString() : uuid.toString();
        }

        public void setId(Integer i) {
            this.id = i;
        }

        public boolean hasId() {
            return id != null;
        }

        public static class Deserializer implements JsonDeserializer<TextureId> {
            @Override
            public TextureId deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
                if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                    var str = element.getAsString();
                    var uuid = CachedUuidDeserializer.get(str);
                    if (uuid == null) {
                        uuid = UUID.fromString(str);
                        CachedUuidDeserializer.put(str, uuid);
                    }
                    return new TextureId(uuid);
                }

                return new TextureId(element.getAsInt());
            }
        }
    }
}

