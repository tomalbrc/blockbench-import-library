package de.tomalbrc.bil.file.extra;

import de.tomalbrc.bil.file.bbmodel.BbElement;
import de.tomalbrc.bil.file.bbmodel.BbFace;
import de.tomalbrc.bil.file.bbmodel.BbTexture;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ResourcePackModel {
    public record DisplayTransform(Vector3f rotation, Vector3f translation, Vector3f scale) {
    }

    public static ResourcePackModel.DisplayTransform DEFAULT_TRANSFORM = new ResourcePackModel.DisplayTransform(new Vector3f(0,180,0), null, null); // default BIL model transform

    private final String parent;
    private final Map<String, ResourceLocation> textures;
    private final List<BbElement> elements;
    private final Map<String, DisplayTransform> display;

    ResourcePackModel(String parent, Map<String, ResourceLocation> textures, List<BbElement> elements, Map<String, DisplayTransform> transformMap) {
        this.parent = parent;
        this.textures = textures;
        this.elements = elements;
        if (transformMap == null) {
            Object2ObjectArrayMap<String, DisplayTransform> map = new Object2ObjectArrayMap<>();
            this.display = map;
        } else {
            this.display = transformMap;
        }
    }

    public byte[] getBytes() {
        return BbResourcePackGenerator.gson.toJson(this).getBytes(StandardCharsets.UTF_8);
    }

    public static class Builder {
        final String modelId;

        String parent = null;

        Map<String, ResourceLocation> textureMap = null;
        List<BbElement> elements = null;

        Map<String, DisplayTransform> transformMap = new Object2ObjectArrayMap<>();

        public Builder(String modelId) {
            this.modelId = modelId;
        }

        public Builder withParent(String parent) {
            this.parent = parent;
            return this;
        }

        public Builder withTextures(Int2ObjectOpenHashMap<BbTexture> intTextureMap) {
            this.textureMap = new Object2ObjectLinkedOpenHashMap<>();
            if (intTextureMap != null) {
                for (var entry : intTextureMap.int2ObjectEntrySet()) {
                    var str = entry.getValue().name;
                    while (str.endsWith(".png")) { // remove all .png extensions if multiple
                        str = str.substring(0, str.length()-4);
                    }
                    this.textureMap.put(String.valueOf(entry.getIntKey()), ResourceLocation.fromNamespaceAndPath("bil", "item/" + this.modelId + "/" + str));
                }
            }
            return this;
        }

        public Builder withElements(List<BbElement> elements) {
            this.elements = elements;
            return this;
        }

        public Builder addDisplayTransform(String transformName, DisplayTransform transform) {
            this.transformMap.put(transformName, transform);
            return this;
        }


        public ResourcePackModel build() {
            Map<String, ResourceLocation> optimizedTextureMap = new Object2ObjectLinkedOpenHashMap<>();
            if (this.elements != null)
                for (BbElement element : this.elements) {
                    for (Map.Entry<String, BbFace> bbFaceEntry : element.faces.entrySet()) {
                        String n = String.valueOf(bbFaceEntry.getValue().texture);
                        optimizedTextureMap.put(n, this.textureMap.get(n));
                    }
                }
            return new ResourcePackModel(this.parent, optimizedTextureMap, this.elements, this.transformMap);
        }
    }
}
