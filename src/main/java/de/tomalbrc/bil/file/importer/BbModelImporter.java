package de.tomalbrc.bil.file.importer;

import de.tomalbrc.bil.core.model.*;
import de.tomalbrc.bil.file.bbmodel.*;
import de.tomalbrc.bil.file.extra.BbModelUtils;
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
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class BbModelImporter implements ModelImporter<BbModel> {
    protected final BbModel model;
    public BbModelImporter(BbModel model) {
        this.model = model;
    }

    protected Object2ObjectOpenHashMap<UUID, Node> makeNodeMap() {
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

    protected void createBones(Node parent, BbOutliner parentOutliner, Collection<BbOutliner.ChildEntry> children, Object2ObjectOpenHashMap<UUID, Node> nodeMap, ObjectArraySet<BbTexture> textures) {
        for (BbOutliner.ChildEntry x: children) {
            if (x.isNode()) {
                BbOutliner outliner = x.outliner;
                PolymerModelData modelData = null;

                if (outliner.hasModel() && outliner.export && !outliner.isHitbox()) {
                    // process model
                    List<BbElement> elements = BbModelUtils.elementsForOutliner(model, outliner);

                    ResourceLocation location = BbResourcePackGenerator.makePart(model, outliner.name.toLowerCase(Locale.US), elements, this.makeDefaultTextureMap());
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

                Node node = new Node(Node.NodeType.BONE, parent, tr, outliner.name, outliner.uuid, modelData);
                nodeMap.put(outliner.uuid, node);

                // children
                createBones(node, outliner, outliner.children, nodeMap, textures);
            }
        }
    }

    protected Quaternionf createQuaternion(Vector3f eulerAngles) {
        return new Quaternionf()
                .rotateZ(Mth.DEG_TO_RAD * eulerAngles.z)
                .rotateY(Mth.DEG_TO_RAD * eulerAngles.y)
                .rotateX(Mth.DEG_TO_RAD * eulerAngles.x);
    }

    protected Reference2ObjectOpenHashMap<UUID, Pose> defaultPose(Object2ObjectOpenHashMap<UUID, Node> nodeMap) {
        Reference2ObjectOpenHashMap<UUID, Pose> res = new Reference2ObjectOpenHashMap<>();

        for (var entry: nodeMap.entrySet()) {
            var bone = entry.getValue();
            if (bone.modelData() != null)
                res.put(bone.uuid(), Pose.of(bone.transform().globalTransform().scale(bone.transform().scale())));
        }

        return res;
    }

    @NotNull
    protected Reference2ObjectOpenHashMap<UUID, Variant> variants() {
        return new Reference2ObjectOpenHashMap<>();
    }

    protected List<Node> nodePath(Node child) {
        List<Node> nodePath = new ObjectArrayList<>();
        while (child != null) {
            nodePath.add(0, child);
            child = child.parent();
        }
        return nodePath;
    }

    @NotNull
    protected Reference2ObjectOpenHashMap<UUID, Pose> poses(BbAnimation animation, Object2ObjectOpenHashMap<UUID, Node> nodeMap, MolangEnvironment environment, float time) throws MolangRuntimeException {
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

    @Nullable
    protected Frame.Variant frameVariant(BbAnimation anim, float t) {
        return null;
    }

    @Nullable
    protected Frame.Commands frameCommands(BbAnimation anim, float t) {
        UUID effectsUUID = CachedUuidDeserializer.get("effects");
        if (effectsUUID != null && anim.animators.containsKey(effectsUUID) && anim.animators.get(effectsUUID).type == BbAnimator.Type.effect) {
            BbAnimator animator = anim.animators.get(effectsUUID);
            for (BbKeyframe kf : animator.keyframes) {
                float difference = Mth.ceil(kf.time / 0.05f) * 0.05f; // snap value to 50ms increments
                if (difference == t && kf.channel == BbKeyframe.Channel.timeline) {
                    String key = "script";
                    String script = kf.dataPoints.get(0).get(key).getStringValue();
                    if (!script.isEmpty()) {
                        var cmds = CommandParser.parse(kf.dataPoints.get(0).get(key).getStringValue());
                        return new Frame.Commands(cmds, null);
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    protected SoundEvent frameSound(BbAnimation anim, float t) {
        UUID effectsUUID = CachedUuidDeserializer.get("effects");
        if (effectsUUID != null && anim.animators.containsKey(effectsUUID) && anim.animators.get(effectsUUID).type == BbAnimator.Type.effect) {
            BbAnimator animator = anim.animators.get(effectsUUID);
            for (BbKeyframe kf : animator.keyframes) {
                float difference = Mth.ceil(kf.time / 0.05f) * 0.05f; // snap value to 50ms increments
                if (difference == t && kf.channel == BbKeyframe.Channel.sound) {
                    SoundEvent event = BuiltInRegistries.SOUND_EVENT.get(new ResourceLocation(kf.dataPoints.get(0).get("effect").getStringValue()));
                    return event;
                }
            }
        }
        return null;
    }

    protected Animation.LoopMode convertLoopMode(BbAnimation.LoopMode loopMode) {
        return switch (loopMode) {
            case ONCE -> Animation.LoopMode.ONCE;
            case HOLD -> Animation.LoopMode.HOLD;
            case LOOP -> Animation.LoopMode.LOOP;
        };
    }

    @NotNull
    protected Object2ObjectOpenHashMap<String, Animation> animations(Object2ObjectOpenHashMap<UUID, Node> nodeMap) {
        Object2ObjectOpenHashMap<String, Animation> res = new Object2ObjectOpenHashMap<>();
        float step = 0.05f;

        if (this.model.animations != null) this.model.animations.parallelStream().forEach(anim -> {
            try {
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
                Animation animation = new Animation(frames, startDelay, loopDelay, frameCount, this.convertLoopMode(anim.loop), affectedBones, false);

                res.put(anim.name, animation);
            } catch (MolangRuntimeException e) {
                throw new RuntimeException(e);
            }
        });

        return res;
    }

    protected Int2ObjectOpenHashMap<BbTexture> makeDefaultTextureMap() {
        Int2ObjectOpenHashMap<BbTexture> textureMap = new Int2ObjectOpenHashMap<>();
        for (BbTexture e: model.textures) textureMap.put(e.id, e); // gen default textureMap
        return textureMap;
    }

    @NotNull
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
