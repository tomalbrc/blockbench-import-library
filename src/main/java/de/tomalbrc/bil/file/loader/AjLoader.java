package de.tomalbrc.bil.file.loader;

import com.google.gson.JsonParseException;
import de.tomalbrc.bil.json.*;
import de.tomalbrc.bil.model.Model;
import de.tomalbrc.bil.model.Node;
import de.tomalbrc.bil.model.Variant;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.io.*;
import java.util.UUID;

public class AjLoader implements ModelLoader {
    public Model load(String path, InputStream input, boolean replaceModelData) throws JsonParseException {
        try (Reader reader = new InputStreamReader(input)) {
            Model model = JSON.GSON.fromJson(reader, Model.class);
            if (replaceModelData) {
                this.replaceModelData(model);
            }

            return model;
        } catch (Throwable throwable) {
            throw new JsonParseException("Failed to parse intermediate: " + path, throwable);
        }
    }

    private void replaceModelData(Model model) {
        // Node intermediate data
        Object2ObjectOpenHashMap<UUID, Node> nodeMap = model.nodeMap();
        for (Node entry : nodeMap.values()) {
            if (entry.type().hasModelData()) {
                nodeMap.computeIfPresent(entry.uuid(), ((id, node) -> new Node(
                        node.type(),
                        node.name(),
                        node.uuid(),
                        new Variant.ModelInfo(PolymerResourcePackUtils.requestModel(Items.PAPER, node.modelInfo().resourceLocation()).value(), node.modelInfo().resourceLocation()),
                        node.entityType()
                )));
            }
        }

        // Variant intermediate data
        for (Variant variant : model.variants().values()) {
            Object2ObjectOpenHashMap<UUID, Variant.ModelInfo> models = variant.models();
            for (UUID uuid : models.keySet()) {
                models.computeIfPresent(uuid, ((id, modelInfo) -> new Variant.ModelInfo(
                        PolymerResourcePackUtils.requestModel(Items.PAPER, modelInfo.resourceLocation()).value(),
                        modelInfo.resourceLocation()
                )));
            }
        }
    }

    public Model load(ResourceLocation id) throws IllegalArgumentException, JsonParseException {
        return this.load(id, true);
    }

    public Model load(String path) throws IllegalArgumentException, JsonParseException {
        return this.load(path, true);
    }

    public Model load(ResourceLocation id, boolean replaceModelData) throws IllegalArgumentException, JsonParseException {
        String path = String.format("/ajmodels/%s/%s.json", id.getNamespace(), id.getPath());
        InputStream input = AjLoader.class.getResourceAsStream(path);
        if (input == null) {
            throw new IllegalArgumentException("Model doesn't exist: " + path);
        }

        return this.load(path, input, replaceModelData);
    }

    public Model load(String path, boolean replaceModelData) throws IllegalArgumentException, JsonParseException {
        try (InputStream input = new FileInputStream(path)) {
            return this.load(path, input, replaceModelData);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Model doesn't exist: " + path);
        }
    }

    @Override
    public Model load(String path, InputStream input) throws JsonParseException {
        return this.load(path, input, true);
    }
}
