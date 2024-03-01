package de.tomalbrc.bil.file.loader;

import com.google.gson.JsonParseException;
import de.tomalbrc.bil.datagen.RPDataGenerator;
import de.tomalbrc.bil.file.bbmodel.BbModel;
import de.tomalbrc.bil.file.importer.BBModelImporter;
import de.tomalbrc.bil.json.JSON;
import de.tomalbrc.bil.core.model.Model;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class BBModelLoader implements ModelLoader {

    @Override
    public Model load(String path, InputStream input) throws JsonParseException {
        try (Reader reader = new InputStreamReader(input)) {
            BbModel model = JSON.BUILDER.create().fromJson(reader, BbModel.class);

            Model newModel = new BBModelImporter().importModel(model);

            RPDataGenerator.makeTextures(model);

            return newModel;
        } catch (Throwable throwable) {
            throw new JsonParseException("Failed to parse intermediate: " + path, throwable);
        }
    }

    public Model load(String name) throws IllegalArgumentException, JsonParseException {
        String path = String.format("/bbmodel/%s.bbmodel", name);
        InputStream input = AjLoader.class.getResourceAsStream(path);
        if (input == null) {
            throw new IllegalArgumentException("Model doesn't exist: " + path);
        }

        return this.load(path, input);
    }
}
