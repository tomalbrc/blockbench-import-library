package de.tomalbrc.bil.datagen;

import de.tomalbrc.bil.file.bbmodel.*;
import de.tomalbrc.bil.json.ElementSerializer;
import de.tomalbrc.bil.json.JSON;
import de.tomalbrc.bil.util.RPUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.resources.ResourceLocation;

import java.util.Base64;
import java.util.List;
import java.util.Map;

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
        return new ResourceLocation("bil:item/generated/" + id + "/" + outliner.name);
    }

    public static void makePart(BbModel model, String partName, List<Element> elements, List<Texture> textures) {
        String id = model.modelIdentifier != null && !model.modelIdentifier.isEmpty() ? model.modelIdentifier : model.name.trim().replace(" ", "_");

        Map<String, ResourceLocation> textureMap = new Object2ObjectLinkedOpenHashMap<>();
        for (Texture texture: model.textures) {
            textureMap.put(texture.id, new ResourceLocation("bil:item/generated/" + id + "/" + texture.name));
        }

        var generatedModel = new GeneratedModel(textureMap, elements);
        var gson = JSON.BUILDER.registerTypeAdapter(Element.class, new ElementSerializer()).create();
        RPUtil.add(new ResourceLocation(":assets/bil/models/item/generated/" + id + "/" + partName + ".json"), gson.toJson(generatedModel).getBytes());
    }

    public static void makeTextures(BbModel model) {
        String id = model.modelIdentifier != null && !model.modelIdentifier.isEmpty() ? model.modelIdentifier : model.name.trim().replace(" ", "_");

        for (Texture texture: model.textures) {
            byte[] texData = Base64.getDecoder().decode(texture.source.replace("data:image/png;base64,", ""));
            RPUtil.add(new ResourceLocation(":assets/bil/textures/item/generated/" + id + "/" + texture.name + ".png"), texData);
        }
    }
}
