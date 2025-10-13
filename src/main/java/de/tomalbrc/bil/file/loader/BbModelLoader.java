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
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class BbModelLoader implements ModelLoader {
    protected static Gson GSON = JSON.GENERIC_BUILDER
            .registerTypeAdapter(BbOutliner.ChildEntry.class, new ChildEntryDeserializer())
            .registerTypeAdapter(BbKeyframe.DataPointValue.class, new DataPointValueDeserializer())
            .registerTypeAdapter(BbVariablePlaceholders.class, new BbVariablePlaceholdersDeserializer())
            .setStrictness(Strictness.LENIENT)
            .create();

    static public Model load(ResourceLocation resourceLocation) {
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
    public Model load(InputStream input, @NotNull String name) throws JsonParseException {
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
    public Model loadResource(ResourceLocation resourceLocation) throws IllegalArgumentException, JsonParseException {
        String path = String.format("/model/%s/%s.bbmodel", resourceLocation.getNamespace(), resourceLocation.getPath());
        InputStream input = BbModelLoader.class.getResourceAsStream(path);
        if (input == null) {
            throw new IllegalArgumentException("Model doesn't exist: " + path);
        }

        return this.load(input, resourceLocation.getPath());
    }
}
