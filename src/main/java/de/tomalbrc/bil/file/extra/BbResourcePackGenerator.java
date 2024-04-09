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
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class BbResourcePackGenerator {
    static Gson gson = JSON.GENERIC_BUILDER
            .registerTypeAdapter(BbFace.class, new FaceSerializer())
            .registerTypeAdapter(BbElement.class, new ElementSerializer())
            .create();

    static ResourcePackItemModel.DisplayTransform DEFAULT_TRANSFORM = new ResourcePackItemModel.DisplayTransform(new Vector3f(0,180,0), null, null); // default BIL model transform

    static String BASE64_PNG_PREFIX = "data:image/png;base64,";
    static String MODEL_DIR = ":assets/bil/models/item/";
    static String TEXTURE_DIR = ":assets/bil/textures/item/";

    public static ResourceLocation addModelPart(BbModel model, String partName, ResourcePackItemModel resourcePackItemModel) {
        ResourceLocation modelResourceLocation = new ResourceLocation(MODEL_DIR + model.modelIdentifier + "/" + partName + ".json");
        ResourcePackUtil.add(modelResourceLocation, resourcePackItemModel.getBytes());

        return new ResourceLocation("bil:item/" + model.modelIdentifier + "/" + partName);
    }

    public static void makeTextures(BbModel model, Collection<BbTexture> textures) {
        for (BbTexture texture : textures) {
            byte[] texData = Base64.getDecoder().decode(texture.source.replace(BASE64_PNG_PREFIX, ""));
            ResourcePackUtil.add(new ResourceLocation(TEXTURE_DIR + model.modelIdentifier + "/" + texture.name + (texture.name.endsWith(".png") ? "":".png")), texData);
        }
    }
}
