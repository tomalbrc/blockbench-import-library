package de.tomalbrc.bil.file.loader;

import com.google.gson.JsonParseException;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.bbmodel.BbModel;
import de.tomalbrc.bil.file.importer.AjModelImporter;
import net.minecraft.resources.ResourceLocation;

import java.io.*;

public class AjModelLoader extends BbModelLoader {
    @Override
    public Model load(InputStream input, String name) throws JsonParseException {
        try (Reader reader = new InputStreamReader(input)) {
            BbModel model = GSON.fromJson(reader, BbModel.class);

            if (name != null && !name.isEmpty()) {
                model.modelIdentifier = name;
            }

            this.postProcess(model);

            return new AjModelImporter(model).importModel();
        } catch (Throwable throwable) {
            throw new JsonParseException("Failed to parse: " + name, throwable);
        }
    }
    @Override
    public Model loadResource(ResourceLocation resourceLocation) throws IllegalArgumentException, JsonParseException {
        String path = String.format("/model/%s/%s.ajmodel", resourceLocation.getNamespace(), resourceLocation.getPath());
        InputStream input = BbModelLoader.class.getResourceAsStream(path);
        if (input == null) {
            throw new IllegalArgumentException("Model doesn't exist: " + path);
        }

        return this.load(input, resourceLocation.getPath());
    }

    static public Model load(ResourceLocation resourceLocation) {
        return new AjModelLoader().loadResource(resourceLocation);
    }

    static public Model load(String path) {
        try (InputStream input = new FileInputStream(path)) {
            return new AjModelLoader().load(input, path);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Model doesn't exist: " + path);
        }
    }
}
