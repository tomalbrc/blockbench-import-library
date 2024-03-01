package de.tomalbrc.bil.file.loader;

import com.google.gson.JsonParseException;
import de.tomalbrc.bil.file.bbmodel.BbModel;
import de.tomalbrc.bil.file.importer.BBModelImporter;
import de.tomalbrc.bil.json.JSON;
import de.tomalbrc.bil.model.Model;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class BBModelLoader implements ModelLoader {

    @Override
    public Model load(String path, InputStream input) throws JsonParseException {
        try (Reader reader = new InputStreamReader(input)) {
            BbModel model = JSON.BUILDER.create().fromJson(reader, BbModel.class);
            return new BBModelImporter().importModel(model);
        } catch (Throwable throwable) {
            throw new JsonParseException("Failed to parse intermediate: " + path, throwable);
        }
    }
}
