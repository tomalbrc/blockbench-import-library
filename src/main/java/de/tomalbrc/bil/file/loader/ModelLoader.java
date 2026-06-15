package de.tomalbrc.bil.file.loader;

import de.tomalbrc.bil.core.model.Model;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

public interface ModelLoader {
    static String normalizedModelId(String id) {
        id = id.trim().replace(" ", "_");
        id = id.replace("-", "_");
        id = id.replace("\\", "/");
        return id.toLowerCase();
    }

    Model load(InputStream input, @NotNull String path);

    Model loadResource(Identifier resourceLocation);
}
