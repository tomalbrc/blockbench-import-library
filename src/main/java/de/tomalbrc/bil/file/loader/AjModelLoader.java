package de.tomalbrc.bil.file.loader;

import com.google.gson.JsonParseException;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.bbmodel.BbModel;
import de.tomalbrc.bil.file.importer.AjModelImporter;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

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
    public Model loadResource(String name) throws IllegalArgumentException, JsonParseException {
        String path = String.format("/model/%s.ajmodel", name);
        InputStream input = BbModelLoader.class.getResourceAsStream(path);
        if (input == null) {
            throw new IllegalArgumentException("Model doesn't exist: " + path);
        }

        return this.load(input, name);
    }
}
