package de.tomalbrc.bil.file.loader;

import com.google.gson.JsonParseException;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.bbmodel.BbElement;
import de.tomalbrc.bil.file.bbmodel.BbFace;
import de.tomalbrc.bil.file.bbmodel.BbModel;
import de.tomalbrc.bil.file.bbmodel.BbTexture;
import de.tomalbrc.bil.file.importer.AjBlueprintImporter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.io.*;
import java.util.List;

public class AjBlueprintLoader extends BbModelLoader {

    @Override
    protected void rescaleUV(Vector2i globalResolution, List<BbTexture> textures, BbElement element) {
        for (var entry : element.faces.entrySet()) {
            // re-map uv based on texture size
            BbFace face = entry.getValue();
            for (int i = 0; i < face.uv.size(); i++) {
                face.uv.set(i, (face.uv.get(i)*16f) / globalResolution.get(i % 2));
            }
        }
    }

    @Override
    public Model load(InputStream input, @NotNull String name) throws JsonParseException {
        try (Reader reader = new InputStreamReader(input)) {
            BbModel model = GSON.fromJson(reader, BbModel.class);

            if (name != null && !name.isEmpty()) model.modelIdentifier = name;
            if (model.modelIdentifier == null) model.modelIdentifier = model.name;
            model.modelIdentifier = ModelLoader.normalizedModelId(model.modelIdentifier);

            this.postProcess(model);

            return new AjBlueprintImporter(model).importModel();
        } catch (Throwable throwable) {
            throw new JsonParseException("Failed to parse: " + name, throwable);
        }
    }
    @Override
    public Model loadResource(ResourceLocation resourceLocation) throws IllegalArgumentException, JsonParseException {
        String path = String.format("/model/%s/%s.ajblueprint", resourceLocation.getNamespace(), resourceLocation.getPath());
        InputStream input = BbModelLoader.class.getResourceAsStream(path);
        if (input == null) {
            throw new IllegalArgumentException("Model doesn't exist: " + path);
        }

        return this.load(input, resourceLocation.getPath());
    }

    static public Model load(ResourceLocation resourceLocation) {
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
