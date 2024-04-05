package de.tomalbrc.bil.file.loader;

import com.google.gson.JsonParseException;
import de.tomalbrc.bil.core.model.Model;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;

public interface ModelLoader {
    Model load(InputStream input, @Nullable String path) throws JsonParseException;
}
