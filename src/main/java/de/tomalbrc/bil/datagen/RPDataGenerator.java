package de.tomalbrc.bil.datagen;

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

public class RPDataGenerator {
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
    }


    public static ResourceLocation locationOf(BbModel model, String outliner) {
        String id = RPDataGenerator.normalizedModelId(model);
        return new ResourceLocation("bil:item/" + id + "/" + outliner);
    }

    public static void makePart(BbModel model, String partName, List<BbElement> elements, List<BbTexture> textures) {
        String id = RPDataGenerator.normalizedModelId(model);

        Map<String, ResourceLocation> textureMap = new Object2ObjectLinkedOpenHashMap<>();
        for (BbTexture texture: model.textures) {
            textureMap.put(texture.id, new ResourceLocation("bil:item/" + id + "/" + texture.name));
        }

        GeneratedModel generatedModel = new GeneratedModel(textureMap, elements);
        Gson gson = JSON.BUILDER
                .registerTypeAdapter(BbFace.class, new FaceSerializer())
                .registerTypeAdapter(BbElement.class, new ElementSerializer())
                .create();
        RPUtil.add(new ResourceLocation(":assets/bil/models/item/" + id + "/" + partName + ".json"), gson.toJson(generatedModel).getBytes());
    }

    public static void makeTextures(BbModel model, List<BbTexture> textures) {
        String id = RPDataGenerator.normalizedModelId(model);

        for (BbTexture texture: textures) {
            byte[] texData = Base64.getDecoder().decode(texture.source.replace("data:image/png;base64,", ""));
            RPUtil.add(new ResourceLocation(":assets/bil/textures/item/" + id + "/" + texture.name + ".png"), texData);
        }
    }

    static private String normalizedModelId(BbModel model) {
        String id = model.modelIdentifier != null && !model.modelIdentifier.isEmpty() ? model.modelIdentifier : model.name.trim().replace(" ", "_");
        id = id.trim().replace(" ", "_");
        id = id.replace("-", "_");
        return id;
    }
}
