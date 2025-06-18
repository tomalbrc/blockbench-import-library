package de.tomalbrc.bil.file.loader;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.Strictness;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.bbmodel.*;
import de.tomalbrc.bil.file.extra.BbModelUtils;
import de.tomalbrc.bil.file.extra.BbVariablePlaceholders;
import de.tomalbrc.bil.file.importer.BbModelImporter;
import de.tomalbrc.bil.json.BbVariablePlaceholdersDeserializer;
import de.tomalbrc.bil.json.ChildEntryDeserializer;
import de.tomalbrc.bil.json.DataPointValueDeserializer;
import de.tomalbrc.bil.json.JSON;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.io.*;
import java.util.List;

public class BbModelLoader implements ModelLoader {
    protected static Gson GSON = JSON.GENERIC_BUILDER
            .registerTypeAdapter(BbOutliner.ChildEntry.class, new ChildEntryDeserializer())
            .registerTypeAdapter(BbKeyframe.DataPointValue.class, new DataPointValueDeserializer())
            .registerTypeAdapter(BbVariablePlaceholders.class, new BbVariablePlaceholdersDeserializer())
            .setStrictness(Strictness.LENIENT)
            .create();

    static public Model load(ResourceLocation resourceLocation) {
        return new BbModelLoader().loadResource(resourceLocation);
    }

    static public Model load(String path) {
        try (InputStream input = new FileInputStream(path)) {
            return new BbModelLoader().load(input, FilenameUtils.getBaseName(path));
        } catch (IOException exception) {
            throw new IllegalArgumentException("Model doesn't exist: " + path);
        }
    }

    private void rescaleUV(Vector2i res, List<BbTexture> textures, BbElement element) {
        for (var entry : element.faces.entrySet()) {
            // re-map uv based on texture size
            BbFace face = entry.getValue();
            for (int i = 0; i < face.uv.size(); i++) {
                Vector2i scaling = null;
                int largestWidth = 0;
                int largestHeight = 0;
                for (BbTexture currentTexture : textures) {
                    if (currentTexture.id == face.texture && currentTexture.width != 0 && currentTexture.height != 0)
                        scaling = new Vector2i(
                                currentTexture.uvWidth != 0 ? currentTexture.uvWidth : currentTexture.width,
                                currentTexture.uvHeight != 0 ? currentTexture.uvHeight : currentTexture.height);

                    if (currentTexture.width > largestWidth)
                        largestWidth = currentTexture.width;

                    if (currentTexture.height > largestHeight)
                        largestHeight = currentTexture.height;
                }

                if (scaling == null && largestHeight != 0 && largestWidth != 0) {
                    scaling = new Vector2i(largestWidth, largestHeight);
                } else if (scaling == null) {
                    scaling = res;
                }

                face.uv.set(i, (face.uv.get(i) * 16.f) / scaling.get(i % 2));
            }
        }
    }

    private void inflateElement(BbElement element) {
        element.from.sub(element.inflate, element.inflate, element.inflate);
        element.to.add(element.inflate, element.inflate, element.inflate);
    }

    protected void postProcess(BbModel model) {
        for (BbElement element : model.elements) {
            if (element.type != BbElement.ElementType.CUBE_MODEL) continue;

            // remove elements without texture
            element.faces.entrySet().removeIf(entry -> entry.getValue().texture == null);

            this.rescaleUV(model.resolution, model.textures, element);
            this.inflateElement(element);

            BbOutliner parent = BbModelUtils.getParent(model, element);
            if (parent != null) {
                element.from.sub(parent.origin);
                element.to.sub(parent.origin);
            }
        }

        for (BbOutliner parent : BbModelUtils.modelOutliner(model)) {
            Vector3f min = new Vector3f(), max = new Vector3f();
            // find max for scale (aj compatibility)
            for (var childEntry : parent.children) {
                if (!childEntry.isNode()) {
                    BbElement element = BbModelUtils.getElement(model, childEntry.uuid);
                    if (element != null && element.type == BbElement.ElementType.CUBE_MODEL) {
                        min.min(element.from);
                        max.max(element.to);
                    }
                }
            }

            for (var childEntry : parent.children) {
                if (!childEntry.isNode()) {
                    BbElement element = BbModelUtils.getElement(model, childEntry.uuid);
                    if (element == null || element.type != BbElement.ElementType.CUBE_MODEL) continue;

                    var diff = min.sub(max, new Vector3f()).absolute();
                    float m = diff.get(diff.maxComponent());
                    float scale = Math.min(1.f, 24.f / m);

                    // for animation + default pose later, to allow for larger models
                    parent.scale = 1.f / scale;

                    element.from.mul(scale).add(8, 8, 8);
                    element.to.mul(scale).add(8, 8, 8);

                    element.origin.sub(parent.origin).mul(scale).add(8, 8, 8);
                }
            }
        }
    }

    @Override
    public Model load(InputStream input, @NotNull String name) throws JsonParseException {
        try (Reader reader = new InputStreamReader(input)) {
            BbModel model = GSON.fromJson(reader, BbModel.class);

            if (!name.isEmpty()) model.modelIdentifier = name;
            if (model.modelIdentifier == null) model.modelIdentifier = model.name;
            model.modelIdentifier = ModelLoader.normalizedModelId(model.modelIdentifier);

            this.postProcess(model);

            return new BbModelImporter(model).importModel();
        } catch (Throwable throwable) {
            throw new JsonParseException("Failed to parse: " + name, throwable);
        }
    }

    @Override
    public Model loadResource(ResourceLocation resourceLocation) throws IllegalArgumentException, JsonParseException {
        String path = String.format("/model/%s/%s.bbmodel", resourceLocation.getNamespace(), resourceLocation.getPath());
        InputStream input = BbModelLoader.class.getResourceAsStream(path);
        if (input == null) {
            throw new IllegalArgumentException("Model doesn't exist: " + path);
        }

        return this.load(input, resourceLocation.getPath());
    }
}
