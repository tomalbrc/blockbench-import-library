package de.tomalbrc.bil.file.loader;

import com.google.gson.JsonParseException;
import de.tomalbrc.bil.model.Model;

import java.io.InputStream;

public class BBModelLoader implements ModelLoader {

    @Override
    public Model load(String path, InputStream input) throws JsonParseException {
        return null;
    }
}
