package de.tomalbrc.bil.file.loader;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
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
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.io.*;

public class BbModelLoader implements ModelLoader {
    static Gson GSON = JSON.GENERIC_BUILDER
            .registerTypeAdapter(BbOutliner.ChildEntry.class, new ChildEntryDeserializer())
            .registerTypeAdapter(BbKeyframe.DataPointValue.class, new DataPointValueDeserializer())
            .registerTypeAdapter(BbVariablePlaceholders.class, new BbVariablePlaceholdersDeserializer())
            .setLenient()
            .create();

    private void rescaleUV(Vector2i res, BbElement element) {
        for (var entry: element.faces.entrySet()) {
            // re-map uv based on resolution
            BbFace face = entry.getValue();
            for (int i = 0; i < face.uv.size(); i++) {
                face.uv.set(i, (face.uv.get(i) * 16.f) / res.get(i % 2));
            }
        }
    }

    private void inflateElement(BbElement element) {
        element.from.sub(element.inflate, element.inflate, element.inflate);
        element.to.add(element.inflate, element.inflate, element.inflate);
    }

    protected void postProcess(BbModel model) {
        for (BbElement element: model.elements) {
            if (element.type != BbElement.ElementType.CUBE) continue;

            this.rescaleUV(model.resolution, element);
            this.inflateElement(element);

            BbOutliner parent = BbModelUtils.getParent(model, element);
            element.from.sub(parent.origin);
            element.to.sub(parent.origin);
        }

        for (BbOutliner parent: BbModelUtils.modelOutliner(model)) {
            Vector3f min = new Vector3f(), max = new Vector3f();
            // find max for scale (aj compatibility)
            for (var childEntry: parent.children) {
                if (!childEntry.isNode()) {
                    BbElement element = BbModelUtils.getElement(model, childEntry.uuid);
                    if (element.type == BbElement.ElementType.CUBE) {
                        min.min(element.from);
                        max.max(element.to);
                    }
                }
            }

            for (var childEntry: parent.children) {
                if (!childEntry.isNode()) {
                    BbElement element = BbModelUtils.getElement(model, childEntry.uuid);
                    if (element.type != BbElement.ElementType.CUBE) continue;

                    var diff = min.sub(max, new Vector3f()).absolute();
                    float m = diff.get(diff.maxComponent());
                    float scale = Math.min(1.f, 24.f / m);

                    // for animation + default pose later, to allow for larger models
                    parent.scale = 1.f / scale;

                    element.from.mul(scale).add(8,8,8);
                    element.to.mul(scale).add(8,8,8);

                    element.origin.sub(parent.origin).mul(scale).add(8,8,8);
                }
            }
        }
    }

    @Override
    public Model load(InputStream input, @Nullable String name) throws JsonParseException {
        try (Reader reader = new InputStreamReader(input)) {
            BbModel model = GSON.fromJson(reader, BbModel.class);

            if (name != null && !name.isEmpty()) {
                model.modelIdentifier = name;
            }

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

    static public Model load(ResourceLocation resourceLocation) {
        return new BbModelLoader().loadResource(resourceLocation);
    }

    static public Model load(String path) {
        try (InputStream input = new FileInputStream(path)) {
            return new BbModelLoader().load(input, path);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Model doesn't exist: " + path);
        }
    }
}
