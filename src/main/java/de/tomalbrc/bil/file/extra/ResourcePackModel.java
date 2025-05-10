package de.tomalbrc.bil.file.extra;

import de.tomalbrc.bil.file.bbmodel.BbElement;
import de.tomalbrc.bil.file.bbmodel.BbFace;
import de.tomalbrc.bil.file.bbmodel.BbTexture;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.FilenameUtils;
import org.joml.Vector3f;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ResourcePackModel {
    public static ResourcePackModel.DisplayTransform DEFAULT_TRANSFORM = new ResourcePackModel.DisplayTransform(new Vector3f(0, 180, 0), null, null); // default BIL model transform
    protected final String parent;
    protected final Map<String, ResourceLocation> textures;
    protected final List<BbElement> elements;
    protected final Map<String, DisplayTransform> display;

    public ResourcePackModel(String parent, Map<String, ResourceLocation> textures, List<BbElement> elements, Map<String, DisplayTransform> transformMap) {
        this.parent = parent;
        this.textures = textures;
        if (this.textures != null && !this.textures.isEmpty() && !this.textures.containsKey("particle")) {
            this.textures.put("particle", this.textures.values().iterator().next());
        }
        this.elements = elements;
        this.display = Objects.requireNonNullElseGet(transformMap, Object2ObjectArrayMap::new);
    }

    public byte[] getBytes() {
        return BbResourcePackGenerator.gson.toJson(this).getBytes(StandardCharsets.UTF_8);
    }

    public record DisplayTransform(Vector3f rotation, Vector3f translation, Vector3f scale) {
    }

    public static class Builder {
        protected final String modelId;
        protected String parent = null;

        protected Map<String, ResourceLocation> textureMap = null;
        protected List<BbElement> elements = null;

        protected Map<String, DisplayTransform> transformMap = new Object2ObjectArrayMap<>();

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
                    var str = FilenameUtils.getBaseName(entry.getValue().name.toLowerCase());
                    while (str.endsWith(".png")) { // remove all .png extensions if multiple
                        str = str.substring(0, str.length() - 4);
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
