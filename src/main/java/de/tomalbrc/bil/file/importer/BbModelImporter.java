package de.tomalbrc.bil.file.importer;

import de.tomalbrc.bil.core.model.*;
import de.tomalbrc.bil.file.bbmodel.*;
import de.tomalbrc.bil.file.extra.BbResourcePackGenerator;
import de.tomalbrc.bil.json.CachedUuidDeserializer;
import de.tomalbrc.bil.util.command.CommandParser;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.virtualentity.api.elements.ItemDisplayElement;
import eu.pb4.polymer.virtualentity.api.tracker.DisplayTrackedData;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec2;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class BbModelImporter implements ModelImporter<BbModel> {
    private final BbModel model;
    public BbModelImporter(BbModel model) {
        this.model = model;
    }

    private Object2ObjectOpenHashMap<UUID, Node> makeNodeMap() {
        Object2ObjectOpenHashMap<UUID, Node> nodeMap = new Object2ObjectOpenHashMap<>();
        ObjectArraySet<BbTexture> textures = new ObjectArraySet<>();

        for (BbOutliner.ChildEntry entry: model.outliner) {
            if (entry.isNode()) {
                createBones(null, null, model.outliner, nodeMap, textures);
            }
        }

        BbResourcePackGenerator.makeTextures(model, textures);

        return nodeMap;
    }

    void createBones(Node parent, BbOutliner parentOutliner, Collection<BbOutliner.ChildEntry> children, Object2ObjectOpenHashMap<UUID, Node> nodeMap, ObjectArraySet<BbTexture> textures) {
        for (BbOutliner.ChildEntry x: children) {
            if (x.isNode()) {
                BbOutliner outliner = x.outliner;
                PolymerModelData modelData = null;

                if (outliner.hasModel() && outliner.export && !outliner.isHitbox()) {
                    // process model
                    List<BbElement> elements = new ObjectArrayList<>();
                    for (BbElement element: model.elements) {
                        if (outliner.hasUuidChild(element.uuid)) {
                            elements.add(element);
                        }
                    }

                    String modelName = outliner.name;

                    ResourceLocation location = BbResourcePackGenerator.makePart(model, modelName, elements, model.textures);
                    textures.addAll(model.textures);

                    modelData = PolymerResourcePackUtils.requestModel(Items.LEATHER_HORSE_ARMOR, location);
                }

                Vector3f localPos = parentOutliner != null ? outliner.origin.sub(parentOutliner.origin, new Vector3f()) : new Vector3f(outliner.origin);
                Quaternionf localRot = createQuaternion(outliner.rotation);

                var tr = new Node.Transform(localPos.div(16), localRot, outliner.scale);
                if (parent != null)
                    tr.mul(parent.transform());
                else
                    tr.mul(new Matrix4f().rotateY(Mth.PI));

                Node node = new Node(Node.NodeType.bone, parent, tr, outliner.name, outliner.uuid, this.createBoneDisplay(modelData), modelData);
                nodeMap.put(outliner.uuid, node);

                // children
                createBones(node, outliner, outliner.children, nodeMap, textures);
            }
        }
    }

    @Nullable
    protected ItemDisplayElement createBoneDisplay(PolymerModelData modelData) {
        if (modelData == null)
            return null;

        ItemDisplayElement element = new ItemDisplayElement();
        element.setModelTransformation(ItemDisplayContext.HEAD);
        element.setInvisible(true);
        element.setInterpolationDuration(2);
        element.getDataTracker().set(DisplayTrackedData.TELEPORTATION_DURATION, 3);

        ItemStack itemStack = new ItemStack(modelData.item());
        itemStack.getOrCreateTag().putInt("CustomModelData", modelData.value());
        if (modelData.item() instanceof DyeableLeatherItem dyeableItem) {
            dyeableItem.setColor(itemStack, -1);
        }

        element.setItem(itemStack);
        return element;
    }

    private Quaternionf createQuaternion(Vector3f eulerAngles) {
        return new Quaternionf()
                .rotateZ(Mth.DEG_TO_RAD * eulerAngles.z)
                .rotateY(Mth.DEG_TO_RAD * eulerAngles.y)
                .rotateX(Mth.DEG_TO_RAD * eulerAngles.x);
    }

    private Reference2ObjectOpenHashMap<UUID, Pose> defaultPose(Object2ObjectOpenHashMap<UUID, Node> nodeMap) {
        Reference2ObjectOpenHashMap<UUID, Pose> res = new Reference2ObjectOpenHashMap<>();

        for (var entry: nodeMap.entrySet()) {
            var bone = entry.getValue();
            if (bone.modelData() != null)
                res.put(bone.uuid(), Pose.of(bone.transform().globalTransform().scale(bone.transform().scale())));
        }

        return res;
    }

    private Reference2ObjectOpenHashMap<UUID, Variant> variants() {
        Reference2ObjectOpenHashMap<UUID, Variant> res = new Reference2ObjectOpenHashMap<>();
        return res;
    }

    private List<Node> nodePath(Node child) {
        List<Node> nodePath = new ObjectArrayList<>();
        while (child != null) {
            nodePath.add(0, child);
            child = child.parent();
        }
        return nodePath;
    }

    private Reference2ObjectOpenHashMap<UUID, Pose> poses(BbAnimation animation, Object2ObjectOpenHashMap<UUID, Node> nodeMap, MolangEnvironment environment, float time) throws MolangRuntimeException {
        Reference2ObjectOpenHashMap<UUID, Pose> poses = new Reference2ObjectOpenHashMap<>();

        for (var entry: nodeMap.entrySet()) {
            if (entry.getValue().modelData() != null) {
                Matrix4f matrix4f = new Matrix4f().rotateY(Mth.PI);
                boolean requiresFrame = false;
                List<Node> nodePath = nodePath(entry.getValue());

                for (var node : nodePath) {
                    BbAnimator animator = animation.animators.get(node.uuid());
                    requiresFrame |= animator != null;

                    Vector3fc origin = node.transform().origin();

                    var triple = animator == null ?
                            Triple.of(new Vector3f(), new Vector3f(), new Vector3f(1.f)) :
                            Sampler.sample(animator.keyframes, model.animationVariablePlaceholders, environment, time);

                    Quaternionf localRot = node.transform().rotation().mul(createQuaternion(triple.getMiddle().mul(-1, -1, 1)), new Quaternionf());
                    Vector3f localPos = triple.getLeft().div(16).add(origin);

                    matrix4f.translate(localPos);
                    matrix4f.rotate(localRot);
                    matrix4f.scale(triple.getRight());
                }

                if (requiresFrame)
                    poses.put(entry.getKey(), Pose.of(matrix4f.scale(entry.getValue().transform().scale())));
            }
        }
        return poses;
    }

    private Frame.Variant frameVariant(BbAnimation anim, float t) {
        // Needs custom aj loader to load variant list
//        UUID effectsUUID = CachedUuidDeserializer.get("effects");
//        if (effectsUUID != null && anim.animators.containsKey(effectsUUID)) {
//            BbAnimator animator = anim.animators.get(effectsUUID);
//            if (animator.type == BbAnimator.Type.effect) {
//                for (BbKeyframe kf : animator.keyframes) {
//                    // todo: custom AjModelLoader
//                    if (Math.abs(kf.time-t) < 0.15f && (kf.channel == BbKeyframe.Channel.variants)) { // todo: snap based on "snapping" in anim
//                        UUID key = CachedUuidDeserializer.get(kf.dataPoints.get(0).get("variant").getStringValue());
//                        var cond = kf.dataPoints.get(0).containsKey("executeCondition") ? CommandParser.parse(kf.dataPoints.get(0).get("executeCondition").getStringValue()) : null;
//                        return new Frame.Variant(key, cond);
//                    }
//                }
//            }
//        }
        return null;
    }

    private Frame.Commands frameCommands(BbAnimation anim, float t) {
        UUID effectsUUID = CachedUuidDeserializer.get("effects");
        if (effectsUUID != null && anim.animators.containsKey(effectsUUID) && anim.animators.get(effectsUUID).type == BbAnimator.Type.effect) {
            BbAnimator animator = anim.animators.get(effectsUUID);
            for (BbKeyframe kf : animator.keyframes) {
                // "commands" and "executeCondition" and Channel.commands for animatedjava support
                // todo: custom AjModelLoader
                float difference = Mth.ceil(kf.time / 0.05f) * 0.05f; // todo: snap based on "snapping" in anim
                if (difference == t && (kf.channel == BbKeyframe.Channel.commands || kf.channel == BbKeyframe.Channel.timeline)) {
                    String key = kf.channel == BbKeyframe.Channel.timeline ? "script" : "commands";
                    var script = kf.dataPoints.get(0).get(key).getStringValue();
                    if (!script.isEmpty()) {
                        var cmds = CommandParser.parse(kf.dataPoints.get(0).get(key).getStringValue());
                        var cond = kf.dataPoints.get(0).containsKey("executeCondition") ? CommandParser.parse(kf.dataPoints.get(0).get("executeCondition").getStringValue()) : null;
                        return new Frame.Commands(cmds, cond);
                    }
                }
            }
        }
        return null;
    }

    private SoundEvent frameSound(BbAnimation anim, float t) {
        UUID effectsUUID = CachedUuidDeserializer.get("effects");
        if (effectsUUID != null && anim.animators.containsKey(effectsUUID) && anim.animators.get(effectsUUID).type == BbAnimator.Type.effect) {
            BbAnimator animator = anim.animators.get(effectsUUID);
            for (BbKeyframe kf : animator.keyframes) {
                float difference = Mth.ceil(kf.time / 0.05f) * 0.05f; // todo: snap based on "snapping" in anim
                if (difference == t && kf.channel == BbKeyframe.Channel.sound) {
                    var key = kf.dataPoints.get(0).containsKey("sound") ? "sound" : "effect"; // "sound" for animatedjava models
                    var event = BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(kf.dataPoints.get(0).get(key).getStringValue()));
                    return event;
                }
            }
        }
        return null;
    }


    private Object2ObjectOpenHashMap<String, Animation> animations(Object2ObjectOpenHashMap<UUID, Node> nodeMap) {
        Object2ObjectOpenHashMap<String, Animation> res = new Object2ObjectOpenHashMap<>();
        float step = 0.05f;

        model.animations.parallelStream().forEach(anim -> {
            try {
                //List<Frame> frames = new ObjectArrayList<>();
                int frameCount = Math.round(anim.length / step);
                Frame[] frames = new Frame[frameCount];
                for (int i = 0; i < frameCount; i++) {
                    float time = i * step;

                    MolangEnvironment env = MolangRuntime.runtime()
                            .setQuery("life_time", time)
                            .setQuery("anim_time", time)
                            .create();

                    // pose for bone in list of frames for an animation
                    Reference2ObjectOpenHashMap<UUID, Pose> poses = poses(anim, nodeMap, env, time);
                    frames[i] = new Frame(time, poses, this.frameVariant(anim, time), this.frameCommands(anim, time), this.frameSound(anim, time));
                }

                // todo: cleanup!
                int startDelay = (int) (anim.startDelay != null && NumberUtils.isParsable(anim.startDelay) ? NumberUtils.createFloat(anim.startDelay).floatValue() : 0);
                int loopDelay = (int) (anim.loopDelay != null && NumberUtils.isParsable(anim.loopDelay) ? NumberUtils.createFloat(anim.loopDelay).floatValue() : 0);

                ReferenceOpenHashSet<UUID> affectedBones = new ReferenceOpenHashSet<>();
                Animation animation = new Animation(frames, startDelay, loopDelay, frameCount, anim.loop, affectedBones, false);

                res.put(anim.name, animation);
            } catch (MolangRuntimeException e) {
                throw new RuntimeException(e);
            }
        });

        return res;
    }

    private Vec2 size() {
        // TODO: read from element or outliner
        return new Vec2(0.5f,1.f);
    }

    @Override
    public Model importModel() {
        var nodeMap = this.makeNodeMap();
        var defaultPose = this.defaultPose(nodeMap);
        var animations = this.animations(nodeMap);
        var variants = this.variants();

        Model result = new Model(nodeMap, defaultPose, variants, animations, this.size());

        return result;
    }
}
