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

                Outliner parent = model.getParent(element);
                element.from.add(8,8,8).sub(element.inflate, element.inflate, element.inflate);
                element.to.add(8,8,8).add(element.inflate, element.inflate, element.inflate);

                if (parent != null && parent.origin != null) {
                    element.from.sub(parent.origin);
                    element.to.sub(parent.origin);

                    element.origin.add(8,8,8).sub(parent.origin);
                }

            }

            Model newModel = new BBModelImporter().importModel(model);

            RPDataGenerator.makeTextures(model);

            return newModel;
        } catch (Throwable throwable) {
            throw new JsonParseException("Failed to parse: " + name, throwable);
        }
    }

    public Model load(String name) throws IllegalArgumentException, JsonParseException {
        String path = String.format("/bbmodel/%s.bbmodel", name);
        InputStream input = AjLoader.class.getResourceAsStream(path);
        if (input == null) {
            throw new IllegalArgumentException("Model doesn't exist: " + path);
        }

        return this.load(name, input);
    }
}
