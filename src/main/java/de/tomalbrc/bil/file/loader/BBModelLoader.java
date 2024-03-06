package de.tomalbrc.bil.file.loader;

import com.google.gson.JsonParseException;
import de.tomalbrc.bil.datagen.RPDataGenerator;
import de.tomalbrc.bil.file.bbmodel.*;
import de.tomalbrc.bil.file.importer.BBModelImporter;
import de.tomalbrc.bil.json.ChildEntryDeserializer;
import de.tomalbrc.bil.json.DataPointValueDeserializer;
import de.tomalbrc.bil.json.JSON;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.json.VariablePlaceholdersDeserializer;
import org.joml.Vector3f;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class BBModelLoader implements ModelLoader {

    @Override
    public Model load(String name, InputStream input) throws JsonParseException {
        try (Reader reader = new InputStreamReader(input)) {
            BbModel model = JSON.BUILDER
                    .registerTypeAdapter(Outliner.ChildEntry.class, new ChildEntryDeserializer())
                    .registerTypeAdapter(Keyframe.DataPointValue.class, new DataPointValueDeserializer())
                    .registerTypeAdapter(VariablePlaceholders.class, new VariablePlaceholdersDeserializer())
                    .create()
                    .fromJson(reader, BbModel.class);

            if (model.modelIdentifier == null) {
                model.modelIdentifier = name;
            }

            for (Element element: model.elements) {
                for (var entry: element.faces.entrySet()) {
                    // re-map uv based on resolution
                    Face face = entry.getValue();
                    for (int i = 0; i < face.uv.size(); i++) {
                        face.uv.set(i, (face.uv.get(i) * 16.f) / model.resolution.get(i % 2));
                    }
                }

                var parent = model.getParent(element);

                element.from.sub(element.inflate, element.inflate, element.inflate);
                element.to.add(element.inflate, element.inflate, element.inflate);

                element.from.sub(parent.origin);
                element.to.sub(parent.origin);
            }

            for (Outliner parent: model.modelOutliner()) {
                Vector3f min = new Vector3f(), max = new Vector3f();
                // find max for scale (aj compat)
                for (var childEntry: parent.children) {
                    if (!childEntry.isNode()) {
                        Element element = model.getElement(childEntry.uuid);
                        min.min(element.from);
                        max.max(element.to);
                    }
                }

                for (var childEntry: parent.children) {
                    if (!childEntry.isNode()) {
                        Element element = model.getElement(childEntry.uuid);

                        var diff = min.sub(max, new Vector3f()).absolute();
                        float m = diff.get(diff.maxComponent());
                        float scale = Math.min(1.f, 24.f / m);

                        // for animation + default pose later
                        parent.scale = 1.f / scale;

                        element.from.mul(scale).add(8,8,8);
                        element.to.mul(scale).add(8,8,8);

                        element.origin.sub(parent.origin).mul(scale).add(8,8,8);
                    }
                }
            }

            Model newModel = new BBModelImporter().importModel(model);
            return newModel;
        } catch (Throwable throwable) {
            throw new JsonParseException("Failed to parse: " + name, throwable);
        }
    }

    public Model load(String name) throws IllegalArgumentException, JsonParseException {
        String path = String.format("/bbmodel/%s.bbmodel", name);
        InputStream input = BBModelImporter.class.getResourceAsStream(path);
        if (input == null) {
            throw new IllegalArgumentException("Model doesn't exist: " + path);
        }

        return this.load(name, input);
    }
}
