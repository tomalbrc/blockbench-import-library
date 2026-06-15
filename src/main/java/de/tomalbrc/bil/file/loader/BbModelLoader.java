package de.tomalbrc.bil.file.loader;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.Strictness;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.bbmodel.BbKeyframe;
import de.tomalbrc.bil.file.bbmodel.BbModel;
import de.tomalbrc.bil.file.bbmodel.BbOutliner;
import de.tomalbrc.bil.file.extra.BbVariablePlaceholders;
import de.tomalbrc.bil.file.importer.BbModel5Importer;
import de.tomalbrc.bil.file.importer.BbModelImporter;
import de.tomalbrc.bil.json.BbVariablePlaceholdersDeserializer;
import de.tomalbrc.bil.json.ChildEntryDeserializer;
import de.tomalbrc.bil.json.DataPointValueDeserializer;
import de.tomalbrc.bil.json.JSON;
import de.tomalbrc.bil.util.VersionCheck;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.jarcontents.JarResource;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Optional;

public class BbModelLoader implements ModelLoader {
    protected static Gson GSON = JSON.GENERIC_BUILDER
            .registerTypeAdapter(BbOutliner.ChildEntry.class, new ChildEntryDeserializer())
            .registerTypeAdapter(BbKeyframe.DataPointValue.class, new DataPointValueDeserializer())
            .registerTypeAdapter(BbVariablePlaceholders.class, new BbVariablePlaceholdersDeserializer())
            .setStrictness(Strictness.LENIENT)
            .create();

    static public Model load(Identifier resourceLocation) {
        return new BbModelLoader().loadResource(resourceLocation);
    }

    static public Model load(String path) {
        try (InputStream input = new FileInputStream(path)) {
            return new BbModelLoader().load(input, FilenameUtils.getBaseName(path));
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

            if (VersionCheck.isAtLeastVersion(model.meta.formatVersion, "5.0.0"))
                return new BbModel5Importer(model).importModel();

            return new BbModelImporter(model).importModel();
        } catch (Throwable throwable) {
            throw new JsonParseException("Failed to parse: " + name, throwable);
        }
    }

    @Override
    public Model loadResource(Identifier resourceLocation) {
        String namespace = resourceLocation.getNamespace();
        String filename = resourceLocation.getPath() + ".bbmodel";

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
