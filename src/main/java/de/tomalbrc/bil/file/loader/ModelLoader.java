package de.tomalbrc.bil.file.loader;

import com.google.gson.JsonParseException;
import de.tomalbrc.bil.core.model.Model;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;

public interface ModelLoader {
    Model load(InputStream input, @Nullable String path) throws JsonParseException;
    Model loadResource(ResourceLocation resourceLocation) throws IllegalArgumentException, JsonParseException;

    static String normalizedModelId(String id) {
        id = id.trim().replace(" ", "_");
        id = id.replace("-", "_");
        return id.toLowerCase();
    }
}
