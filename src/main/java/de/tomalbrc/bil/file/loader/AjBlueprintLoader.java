package de.tomalbrc.bil.file.loader;

import com.google.gson.JsonParseException;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.bbmodel.BbModel;
import de.tomalbrc.bil.file.importer.AjModelImporter;
import net.minecraft.resources.ResourceLocation;

import java.io.*;

public class AjBlueprintLoader extends BbModelLoader {
    @Override
    public Model load(InputStream input, String name) throws JsonParseException {
        try (Reader reader = new InputStreamReader(input)) {
            BbModel model = GSON.fromJson(reader, BbModel.class);

            if (name != null && !name.isEmpty()) model.modelIdentifier = name;
            if (model.modelIdentifier == null) model.modelIdentifier = model.name;
            model.modelIdentifier = ModelLoader.normalizedModelId(model.modelIdentifier);

            this.postProcess(model);

            return new AjModelImporter(model).importModel();
        } catch (Throwable throwable) {
            throw new JsonParseException("Failed to parse: " + name, throwable);
        }
    }
    @Override
    public Model loadResource(ResourceLocation resourceLocation) throws IllegalArgumentException, JsonParseException {
        String path = String.format("/model/%s/%s.ajblueprint", resourceLocation.getNamespace(), resourceLocation.getPath());
        InputStream input = BbModelLoader.class.getResourceAsStream(path);
        if (input == null) {
            throw new IllegalArgumentException("Model doesn't exist: " + path);
        }

        return this.load(input, resourceLocation.getPath());
    }

    static public Model load(ResourceLocation resourceLocation) {
        return new AjBlueprintLoader().loadResource(resourceLocation);
    }

    static public Model load(String path) {
        try (InputStream input = new FileInputStream(path)) {
            return new AjBlueprintLoader().load(input, path);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Model doesn't exist: " + path);
        }
    }
}
