package de.tomalbrc.bil.file.importer;

import de.tomalbrc.bil.core.model.Variant;
import de.tomalbrc.bil.file.ajmodel.AjVariant;
import de.tomalbrc.bil.file.bbmodel.*;
import de.tomalbrc.bil.file.extra.BbModelUtils;
import de.tomalbrc.bil.file.extra.BbResourcePackGenerator;
import de.tomalbrc.bil.file.extra.ResourcePackItemModel;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class AjBlueprintImporter extends AjModelImporter implements ModelImporter<BbModel> {
    public AjBlueprintImporter(BbModel model) {
        super(model);
    }

    @NotNull
    private ReferenceOpenHashSet<UUID> affectedBones(AjVariant variant) {
        ReferenceOpenHashSet<UUID> affectedBoneIds = new ReferenceOpenHashSet<>();
        if (variant.affectedBones() != null && !variant.affectedBones().isEmpty()) {
            for (AjVariant.AffectedBoneEntry entry : variant.affectedBones()) {
                affectedBoneIds.add(entry.value());
            }
        }
        return affectedBoneIds;
    }

    @Override
    @NotNull
    protected Reference2ObjectOpenHashMap<UUID, Variant> variants() {
        if (this.model.ajMeta != null && this.model.ajMeta.variants() != null) {
            Reference2ObjectOpenHashMap<UUID, Variant> res = new Reference2ObjectOpenHashMap<>();
            for (AjVariant variant : this.model.variants.list()) {
                // generate more models
                var affectedBones = this.affectedBones(variant);

                Reference2ObjectOpenHashMap<UUID, ResourceLocation> models = new Reference2ObjectOpenHashMap<>();

                for (BbOutliner outliner : BbModelUtils.modelOutliner(model)) {
                    boolean affected = affectedBones.contains(outliner.uuid) && variant.affectedBonesIsAWhitelist() ||
                            !variant.affectedBonesIsAWhitelist() && !affectedBones.contains(outliner.uuid);

                    if (!outliner.isHitbox() && affected) {
                        List<BbElement> elements = BbModelUtils.elementsForOutliner(model, outliner, BbElement.ElementType.CUBE);

                        Int2ObjectOpenHashMap<BbTexture> textureMap = new Int2ObjectOpenHashMap<>();
                        if (variant.textureMap() == null || variant.textureMap().isEmpty()) {
                            textureMap = makeDefaultTextureMap();
                        } else {
                            for (BbTexture e : model.textures) {
                                BbTexture newMapped = variant.textureMap().containsKey(e.uuid) ? BbModelUtils.getTexture(model, variant.textureMap().get(e.uuid)) : e;
                                textureMap.put(e.id, newMapped);
                            }
                        }

                        ResourcePackItemModel.Builder builder = new ResourcePackItemModel.Builder(model.modelIdentifier)
                                .withTextures(textureMap)
                                .withElements(elements)
                                .addDisplayTransform("head", ResourcePackItemModel.DEFAULT_TRANSFORM);

                        ResourceLocation location = BbResourcePackGenerator.addModelPart(model, String.format("%s_%s", outliner.uuid.toString(), variant.name().toLowerCase()), builder.build());
                        models.put(outliner.uuid, location);
                    }
                }

                res.put(variant.uuid(), new Variant(variant.name(), variant.uuid(), models, affectedBones, variant.affectedBonesIsAWhitelist()));
            }
            return res;
        }
        return new Reference2ObjectOpenHashMap<>();
    }
}
