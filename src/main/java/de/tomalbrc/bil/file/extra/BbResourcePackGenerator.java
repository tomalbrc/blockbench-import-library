package de.tomalbrc.bil.file.extra;

import com.google.gson.Gson;
import de.tomalbrc.bil.file.bbmodel.*;
import de.tomalbrc.bil.json.ElementSerializer;
import de.tomalbrc.bil.json.FaceSerializer;
import de.tomalbrc.bil.json.JSON;
import de.tomalbrc.bil.util.RPUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.*;

public class BbResourcePackGenerator {
    static Gson gson = JSON.GENERIC_BUILDER
            .registerTypeAdapter(BbFace.class, new FaceSerializer())
            .registerTypeAdapter(BbElement.class, new ElementSerializer())
            .create();
    static String BASE64_PNG_PREFIX = "data:image/png;base64,";
    static String MODEL_DIR = ":assets/bil/models/item/";
    static String TEXTURE_DIR = ":assets/bil/textures/item/";

    static class GeneratedModel {
        record Display(DisplayTransform head) {
            record DisplayTransform(Vector3f rotation){}
        };
        private final Map<String, ResourceLocation> textures;
        private final List<BbElement> elements;

        private final Display display;

        GeneratedModel(Map<String, ResourceLocation> textures, List<BbElement> elements) {
            this.textures = textures;
            this.elements = elements;
            this.display = new Display(new Display.DisplayTransform(new Vector3f(0,180,0)));
        }

        public byte[] getBytes() {
            return gson.toJson(this).getBytes();
        }
    }

    public static ResourceLocation makePart(BbModel model, String partName, List<BbElement> elements, List<BbTexture> textures) {
        String id = BbResourcePackGenerator.normalizedModelId(model);

        Map<String, ResourceLocation> textureMap = new Object2ObjectLinkedOpenHashMap<>();
        for (BbTexture texture: model.textures) {
            textureMap.put(texture.id, new ResourceLocation("bil:item/" + id + "/" + texture.name));
        }

        GeneratedModel generatedModel = new GeneratedModel(textureMap, elements);
        ResourceLocation modelResource = new ResourceLocation(MODEL_DIR + id + "/" + partName + ".json");
        RPUtil.add(modelResource, generatedModel.getBytes());

        return modelResource;
    }

    public static void makeTextures(BbModel model, Collection<BbTexture> textures) {
        String id = BbResourcePackGenerator.normalizedModelId(model);

        for (BbTexture texture: textures) {
            byte[] texData = Base64.getDecoder().decode(texture.source.replace(BASE64_PNG_PREFIX, ""));
            RPUtil.add(new ResourceLocation(TEXTURE_DIR + id + "/" + texture.name + ".png"), texData);
        }
    }

    static private String normalizedModelId(BbModel model) {
        String id = model.modelIdentifier != null && !model.modelIdentifier.isEmpty() ? model.modelIdentifier : model.name.trim().replace(" ", "_");
        id = id.trim().replace(" ", "_");
        id = id.replace("-", "_");
        return id;
    }
}
