package de.tomalbrc.bil.file.importer;

import de.tomalbrc.bil.core.model.Frame;
import de.tomalbrc.bil.core.model.Variant;
import de.tomalbrc.bil.file.ajmodel.AjVariant;
import de.tomalbrc.bil.file.bbmodel.*;
import de.tomalbrc.bil.file.extra.BbModelUtils;
import de.tomalbrc.bil.file.extra.BbResourcePackGenerator;
import de.tomalbrc.bil.file.extra.ResourcePackModel;
import de.tomalbrc.bil.json.CachedUuidDeserializer;
import de.tomalbrc.bil.util.command.CommandParser;
import de.tomalbrc.bil.util.command.ParsedCommand;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.List;
import java.util.UUID;

public class AjBlueprintImporter extends AjModelImporter implements ModelImporter<BbModel> {
    public AjBlueprintImporter(BbModel model) {
        super(model);
    }

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
        if (this.model.variants != null) {
            Reference2ObjectOpenHashMap<UUID, Variant> res = new Reference2ObjectOpenHashMap<>();
            for (AjVariant variant : this.model.variants.list()) {
                // generate more models
                var affectedBones = this.affectedBones(variant);

                Reference2ObjectOpenHashMap<UUID, ResourceLocation> models = new Reference2ObjectOpenHashMap<>();

                for (BbOutliner outliner : BbModelUtils.modelOutliner(model)) {
                    boolean affected = affectedBones.contains(outliner.uuid) && variant.affectedBonesIsAWhitelist() ||
                            !variant.affectedBonesIsAWhitelist() && !affectedBones.contains(outliner.uuid);

                    if (!outliner.isHitbox() && affected) {
                        List<BbElement> elements = BbModelUtils.elementsForOutliner(model, outliner, BbElement.ElementType.CUBE_MODEL);

                        Int2ObjectOpenHashMap<BbTexture> textureMap = new Int2ObjectOpenHashMap<>();
                        if (variant.textureMap() == null || variant.textureMap().isEmpty()) {
                            textureMap = makeDefaultTextureMap();
                        } else {
                            for (BbTexture e : model.textures) {
                                BbTexture newMapped = variant.textureMap().containsKey(e.uuid) ? BbModelUtils.getTexture(model, variant.textureMap().get(e.uuid)) : e;
                                textureMap.putIfAbsent(e.id, newMapped);
                            }
                        }

                        ResourcePackModel.Builder builder = new ResourcePackModel.Builder(model.modelIdentifier)
                                .withTextures(textureMap)
                                .withElements(elements)
                                .addDisplayTransform("head", ResourcePackModel.DEFAULT_TRANSFORM);

                        ResourceLocation location = BbResourcePackGenerator.addItemModel(model, String.format("%s_%s", outliner.uuid.toString(), variant.name().toLowerCase()), builder.build());
                        models.put(outliner.uuid, location);
                    }
                }

                res.put(variant.uuid(), new Variant(variant.name(), variant.uuid(), models, affectedBones, variant.affectedBonesIsAWhitelist()));
            }
            return res;
        }
        return new Reference2ObjectOpenHashMap<>();
    }

    @Override
    protected Frame.Variant frameVariant(BbAnimation anim, float t) {
        UUID effectsUUID = CachedUuidDeserializer.get("effects");
        if (effectsUUID != null && anim.animators != null && anim.animators.containsKey(effectsUUID)) {
            BbAnimator animator = anim.animators.get(effectsUUID);
            if (animator.type == BbAnimator.Type.EFFECT) {
                for (BbKeyframe kf : animator.keyframes) {
                    if (Math.abs(kf.time - t) < 0.15f && kf.channel == BbKeyframe.Channel.VARIANTS) { // snap value to 50ms increments
                        UUID key = CachedUuidDeserializer.get(kf.dataPoints.getFirst().get("variant").getStringValue());
                        var cond = kf.dataPoints.getFirst().containsKey("execute_condition") ? CommandParser.parse(kf.dataPoints.getFirst().get("execute_condition").getStringValue()) : null;
                        return new Frame.Variant(key, cond);
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected Frame.Commands frameCommands(BbAnimation anim, float t) {
        UUID effectsUUID = CachedUuidDeserializer.get("effects");
        if (effectsUUID != null && anim.animators != null && anim.animators.containsKey(effectsUUID) && anim.animators.get(effectsUUID).type == BbAnimator.Type.EFFECT) {
            BbAnimator animator = anim.animators.get(effectsUUID);
            for (BbKeyframe kf : animator.keyframes) {
                float difference = Mth.ceil(kf.time / 0.05f) * 0.05f; // snap value to 50ms increments
                if (difference == t && kf.channel == BbKeyframe.Channel.COMMANDS) {
                    var script = kf.dataPoints.getFirst().get("commands").getStringValue();
                    if (!script.isEmpty()) {
                        ParsedCommand[] cmds = CommandParser.parse(kf.dataPoints.getFirst().get("commands").getStringValue());
                        ParsedCommand[] cond = kf.dataPoints.getFirst().containsKey("execute_condition") ? CommandParser.parse(kf.dataPoints.getFirst().get("execute_condition").getStringValue()) : null;
                        return new Frame.Commands(cmds, cond);
                    }
                }
            }
        }
        return null;
    }
}
