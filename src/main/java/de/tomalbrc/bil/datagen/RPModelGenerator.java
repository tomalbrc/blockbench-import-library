package de.tomalbrc.bil.datagen;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import de.tomalbrc.bil.file.bbmodel.BbModel;
import de.tomalbrc.bil.file.bbmodel.Element;
import de.tomalbrc.bil.file.bbmodel.Texture;

import java.util.List;
import java.util.Map;

public class RPModelGenerator {
    static record GeneratedModel(Map<String, ResourceLocation> textures, List<Element> elements) {
    }

    public static void of(BbModel model, List<Element> elements, List<Texture> textures) {
        String id = model.modelIdentifier != null ? model.modelIdentifier : model.name.trim().replace(" ", "_");

        Map<String, ResourceLocation> textureMap = new Object2ObjectLinkedOpenHashMap<>();
        for (Texture texture: model.textures) {
            textureMap.put(texture.id, new ResourceLocation("bil:item/generated/" + id + "/" + texture.name));
        }

        var gmodel = new GeneratedModel(textureMap, model.elements);

    }
}
