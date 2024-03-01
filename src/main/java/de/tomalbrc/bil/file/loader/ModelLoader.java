package de.tomalbrc.bil.file.loader;

import com.google.gson.JsonParseException;
import de.tomalbrc.bil.model.Model;

import java.io.InputStream;

public interface ModelLoader {
    Model load(String path, InputStream input) throws JsonParseException;
}
