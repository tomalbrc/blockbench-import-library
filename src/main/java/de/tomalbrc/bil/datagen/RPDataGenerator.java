package de.tomalbrc.bil.datagen;

import com.google.gson.Gson;
import de.tomalbrc.bil.file.bbmodel.*;
import de.tomalbrc.bil.json.ElementSerializer;
import de.tomalbrc.bil.json.FaceSerializer;
import de.tomalbrc.bil.json.JSON;
import de.tomalbrc.bil.util.RPUtil;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;

public class RPDataGenerator {
    static class GeneratedModel {
        private final Map<String, ResourceLocation> textures;
        private final List<Element> elements;

        GeneratedModel(Map<String, ResourceLocation> textures, List<Element> elements) {
            this.textures = textures;
            this.elements = elements;
        }
    }


    public static ResourceLocation locationOf(BbModel model, Outliner outliner) {
        String id = model.modelIdentifier != null && !model.modelIdentifier.isEmpty() ? model.modelIdentifier : model.name.trim().replace(" ", "_");
        id = id.trim().replace(" ", "_");
        id = id.replace("-", "_");

        return new ResourceLocation("bil:item/generated/" + id + "/" + outliner.name);
    }

    public static void makePart(BbModel model, String partName, List<Element> elements, List<Texture> textures) {
        String id;
        if (model.name == null && model.modelIdentifier == null) {
            id = UUID.randomUUID().toString();
        } else {
            id = model.modelIdentifier != null && !model.modelIdentifier.isEmpty() ? model.modelIdentifier : model.name;
        }

        id = id.trim().replace(" ", "_");
        id = id.replace("-", "_");

        Map<String, ResourceLocation> textureMap = new Object2ObjectLinkedOpenHashMap<>();
        for (Texture texture: model.textures) {
            textureMap.put(texture.id, new ResourceLocation("bil:item/generated/" + id + "/" + texture.name));
        }

        GeneratedModel generatedModel = new GeneratedModel(textureMap, elements);
        Gson gson = JSON.BUILDER
                .registerTypeAdapter(Face.class, new FaceSerializer())
                .registerTypeAdapter(Element.class, new ElementSerializer())
                .create();
        RPUtil.add(new ResourceLocation(":assets/bil/models/item/generated/" + id + "/" + partName + ".json"), gson.toJson(generatedModel).getBytes());
    }

    public static void makeTextures(BbModel model) {
        String id = model.modelIdentifier != null && !model.modelIdentifier.isEmpty() ? model.modelIdentifier : model.name.trim().replace(" ", "_");
        id = id.trim().replace(" ", "_");
        id = id.replace("-", "_");

        for (Texture texture: model.textures) {
            byte[] texData = Base64.getDecoder().decode(texture.source.replace("data:image/png;base64,", ""));
            RPUtil.add(new ResourceLocation(":assets/bil/textures/item/generated/" + id + "/" + texture.name + ".png"), texData);
        }
    }
}
