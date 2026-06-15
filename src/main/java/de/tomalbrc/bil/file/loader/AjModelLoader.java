package de.tomalbrc.bil.file.loader;

import com.google.gson.JsonParseException;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.bbmodel.BbModel;
import de.tomalbrc.bil.file.importer.AjModelImporter;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.jarcontents.JarResource;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Optional;

public class AjModelLoader extends BbModelLoader {
    static public Model load(Identifier resourceLocation) {
        return new AjModelLoader().loadResource(resourceLocation);
    }

    static public Model load(String path) {
        try (InputStream input = new FileInputStream(path)) {
            return new AjModelLoader().load(input, FilenameUtils.getBaseName(path));
        } catch (IOException exception) {
            throw new IllegalArgumentException("Model doesn't exist: " + path);
        }
    }

    @Override
    public Model load(InputStream input, @NotNull String name) {
        try (Reader reader = new InputStreamReader(input)) {
            BbModel model = GSON.fromJson(reader, BbModel.class);

            if (!name.isEmpty()) model.modelIdentifier = name;
            if (model.modelIdentifier == null) model.modelIdentifier = model.name;
            model.modelIdentifier = ModelLoader.normalizedModelId(model.modelIdentifier);

            return new AjModelImporter(model).importModel();
        } catch (Throwable throwable) {
            throw new JsonParseException("Failed to parse: " + name, throwable);
        }
    }

    @Override
    public Model loadResource(Identifier resourceLocation) {
        String namespace = resourceLocation.getNamespace();
        String filename = resourceLocation.getPath() + ".ajmodel";

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
}
