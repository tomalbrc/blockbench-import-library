package de.tomalbrc.bil.file.extra;

import com.google.gson.Gson;
import de.tomalbrc.bil.file.bbmodel.BbElement;
import de.tomalbrc.bil.file.bbmodel.BbFace;
import de.tomalbrc.bil.file.bbmodel.BbModel;
import de.tomalbrc.bil.file.bbmodel.BbTexture;
import de.tomalbrc.bil.json.ElementSerializer;
import de.tomalbrc.bil.json.FaceSerializer;
import de.tomalbrc.bil.json.JSON;
import de.tomalbrc.bil.util.ResourcePackUtil;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.extras.api.format.item.ItemAsset;
import eu.pb4.polymer.resourcepack.extras.api.format.item.model.BasicItemModel;
import eu.pb4.polymer.resourcepack.extras.api.format.item.tint.DyeTintSource;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.FilenameUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

public class BbResourcePackGenerator {
    static Gson gson = JSON.GENERIC_BUILDER
            .registerTypeAdapter(BbFace.class, new FaceSerializer())
            .registerTypeAdapter(BbElement.class, new ElementSerializer())
            .create();

    public static String BASE64_PNG_PREFIX = "data:image/png;base64,";
    static String MODEL_DIR = ":assets/bil/models/item/";
    static String TEXTURE_DIR = ":assets/bil/textures/item/";

    public static ResourceLocation addModelPart(BbModel model, String partName, ResourcePackModel resourcePackModel) {
        ResourceLocation modelResourceLocation = ResourceLocation.parse(MODEL_DIR + model.modelIdentifier + "/" + partName.toLowerCase() + ".json");
        ResourcePackUtil.add(modelResourceLocation, resourcePackModel.getBytes());

        return ResourceLocation.fromNamespaceAndPath("bil", "item/" + model.modelIdentifier + "/" + partName.toLowerCase());
    }

    public static ResourceLocation addItemModel(BbModel model, String partName, ResourcePackModel resourcePackModel) {
        partName = model.modelIdentifier + "_" + partName;
        var modelPath = addModelPart(model, partName, resourcePackModel);

        var defaultModel = new BasicItemModel(modelPath, List.of(new DyeTintSource(0xFFFFFF)));
        var bytes = new ItemAsset(
                defaultModel,
                ItemAsset.Properties.DEFAULT
        ).toJson().getBytes(StandardCharsets.UTF_8);

        var id = ResourceLocation.fromNamespaceAndPath("bil", model.modelIdentifier + "_" + partName);
        ResourcePackUtil.add(ResourceLocation.parse(":" + AssetPaths.itemAsset(id)), bytes);

        return id;
    }

    public static void makeTextures(BbModel model, Collection<BbTexture> textures) {
        for (BbTexture texture : textures) {
            byte[] texData = Base64.getDecoder().decode(texture.source.replace(BASE64_PNG_PREFIX, ""));
            var str = FilenameUtils.getBaseName(texture.name.toLowerCase());
            while (str.endsWith(".png")) { // remove all .png extensions if multiple
                str = str.substring(0, str.length() - 4);
            }
            ResourcePackUtil.add(ResourceLocation.parse(TEXTURE_DIR + model.modelIdentifier + "/" + str + ".png"), texData);
        }
    }
}
