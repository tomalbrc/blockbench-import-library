package de.tomalbrc.bil.file.loader;

import com.google.gson.JsonParseException;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.bbmodel.BbModel;
import de.tomalbrc.bil.file.importer.AjBlueprintImporter;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.jarcontents.JarResource;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Optional;

public class AjBlueprintLoader extends BbModelLoader {
    @Override
    public Model load(InputStream input, @NotNull String name) {
        try (Reader reader = new InputStreamReader(input)) {
            BbModel model = GSON.fromJson(reader, BbModel.class);

            if (name != null && !name.isEmpty()) model.modelIdentifier = name;
            if (model.modelIdentifier == null) model.modelIdentifier = model.name;
            model.modelIdentifier = ModelLoader.normalizedModelId(model.modelIdentifier);

            return new AjBlueprintImporter(model).importModel();
        } catch (Throwable throwable) {
            throw new JsonParseException("Failed to parse: " + name, throwable);
        }
    }

    @Override
    public Model loadResource(Identifier resourceLocation) {
        String namespace = resourceLocation.getNamespace();
        String filename = resourceLocation.getPath() + ".ajblueprint";

        InputStream input = null;
        Optional<? extends ModContainer> container = ModList.get().getModContainerById(namespace);

        Model loaded = null;
        if (container.isPresent()) {
            JarResource jarResource = container.get().getModInfo().getOwningFile().getFile().getContents().get("model/" + namespace + "/" + filename);
            if (jarResource != null) {
                try {
                    input = jarResource.open();
                    loaded = this.load(input, resourceLocation.getPath());
                    input.close();
                } catch (IOException exception) {
                    throw new IllegalArgumentException("Failed to open model: " + filename, exception);
                }
            }
        }

        return loaded;
    }

    static public Model load(Identifier resourceLocation) {
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
