package de.tomalbrc.bil.file.importer;

import de.tomalbrc.bil.core.model.*;
import de.tomalbrc.bil.file.bbmodel.*;
import de.tomalbrc.bil.file.extra.BbModelUtils;
import de.tomalbrc.bil.file.extra.BbResourcePackGenerator;
import de.tomalbrc.bil.file.extra.ResourcePackItemModel;
import de.tomalbrc.bil.json.CachedUuidDeserializer;
import de.tomalbrc.bil.util.command.CommandParser;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec2;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class BbModelImporter implements ModelImporter<BbModel> {
    protected final BbModel model;

    public BbModelImporter(BbModel model) {
        this.model = model;
        postProcess(model);
    }

    protected void rescaleUV(Vector2i globalResolution, List<BbTexture> textures, BbElement element) {
        for (var entry : element.faces.entrySet()) {
            // re-map uv based on texture size
            BbFace face = entry.getValue();
            for (int i = 0; i < face.uv.size(); i++) {
                Vector2i textureResolution = null;
                var texture = textures.get(face.texture);
                if (texture.uvHeight != 0 && texture.uvWidth != 0)
                    textureResolution = new Vector2i(texture.uvWidth, texture.uvHeight);

                if (textureResolution == null) {
                    textureResolution = globalResolution != null ? globalResolution : new Vector2i(16, 16);
                }

                face.uv.set(i, (face.uv.get(i) * 16f) / textureResolution.get(i % 2));
            }
        }
    }

    protected void inflateElement(BbElement element) {
        element.from.sub(element.inflate, element.inflate, element.inflate);
        element.to.add(element.inflate, element.inflate, element.inflate);
    }

    protected void postProcess(BbModel model) {
        for (BbElement element : model.elements) {
            if (element.type != BbElement.ElementType.CUBE_MODEL) continue;

            // remove elements without texture
            element.faces.entrySet().removeIf(entry -> entry.getValue().texture == null);

            this.rescaleUV(model.resolution, model.textures, element);
            this.inflateElement(element);

            BbOutliner parent = BbModelUtils.getParent(model, element);
            if (parent != null) {
                element.from.sub(parent.origin);
                element.to.sub(parent.origin);
            }
        }

        for (BbOutliner parent : BbModelUtils.modelOutliner(model)) {
            Vector3f min = new Vector3f(), max = new Vector3f();
            // find max for scale (aj compatibility)
            for (var childEntry : parent.children) {
                if (!childEntry.isNode()) {
                    BbElement element = BbModelUtils.getElement(model, childEntry.uuid);
                    if (element != null && element.type == BbElement.ElementType.CUBE_MODEL) {
                        min.min(element.from);
                        max.max(element.to);
                    }
                }
            }

            for (var childEntry : parent.children) {
                if (!childEntry.isNode()) {
                    BbElement element = BbModelUtils.getElement(model, childEntry.uuid);
                    if (element == null || element.type != BbElement.ElementType.CUBE_MODEL) continue;

                    var diff = min.sub(max, new Vector3f()).absolute();
                    float m = diff.get(diff.maxComponent());
                    float scale = Math.min(1.f, 24.f / m);

                    // for animation + default pose later, to allow for larger models
                    parent.scale = 1.f / scale;

                    element.from.mul(scale).add(8, 8, 8);
                    element.to.mul(scale).add(8, 8, 8);

                    element.origin.sub(parent.origin).mul(scale).add(8, 8, 8);
                }
            }
        }
    }

    protected Object2ObjectOpenHashMap<UUID, Node> makeNodeMap() {
        Object2ObjectOpenHashMap<UUID, Node> nodeMap = new Object2ObjectOpenHashMap<>();
        ObjectArraySet<BbTexture> textures = new ObjectArraySet<>();
        textures.addAll(model.textures);

        for (BbOutliner.ChildEntry entry : model.outliner) {
            if (entry.isNode()) {
                createBones(null, null, model.outliner, nodeMap);
            }
        }

        BbResourcePackGenerator.makeTextures(model, textures);

        return nodeMap;
    }

    protected PolymerModelData generateModel(BbOutliner outliner) {
        List<BbElement> elements = BbModelUtils.elementsForOutliner(model, outliner, BbElement.ElementType.CUBE);

        ResourcePackItemModel.Builder builder = new ResourcePackItemModel.Builder(model.modelIdentifier)
                .withTextures(this.makeDefaultTextureMap())
                .withElements(elements)
                .addDisplayTransform("head", ResourcePackItemModel.DEFAULT_TRANSFORM);

        ResourceLocation location = BbResourcePackGenerator.addModelPart(model, outliner.uuid.toString(), builder.build());
        return PolymerResourcePackUtils.requestModel(Items.LEATHER_HORSE_ARMOR, location);
    }

    protected void createBones(Node parent, BbOutliner parentOutliner, Collection<BbOutliner.ChildEntry> children, Object2ObjectOpenHashMap<UUID, Node> nodeMap) {
        for (BbOutliner.ChildEntry entry : children) {
            if (entry.isNode()) {
                BbOutliner outliner = entry.outliner;
                PolymerModelData modelData = null;

                if (outliner.hasModel() && outliner.export && !outliner.isHitbox()) {
                    modelData = this.generateModel(outliner);
                }

                Vector3f localPos = parentOutliner != null ? outliner.origin.sub(parentOutliner.origin, new Vector3f()) : new Vector3f(outliner.origin);
                Quaternionf localRot = createQuaternion(outliner.rotation);

                var tr = new Node.Transform(localPos.div(16), localRot, outliner.scale);
                if (parent != null)
                    tr.mul(parent.transform());
                else
                    tr.mul(new Matrix4f().rotateY(Mth.PI));

                Node node = new Node(Node.NodeType.BONE, parent, tr, outliner.name, outliner.uuid, modelData, outliner.name.startsWith("head"));
                nodeMap.put(outliner.uuid, node);

                processLocators(nodeMap, outliner, node);
                processTextDisplays(nodeMap, outliner, node);
                processBlockDisplays(nodeMap, outliner, node);
                processItemDisplays(nodeMap, outliner, node);

                // children
                createBones(node, outliner, outliner.children, nodeMap);
            }
        }
    }

    protected void processLocators(Object2ObjectOpenHashMap<UUID, Node> nodeMap, BbOutliner outliner, Node node) {
        List<BbElement> locatorElements = BbModelUtils.elementsForOutliner(model, outliner, BbElement.ElementType.LOCATOR);
        for (BbElement element : locatorElements) {
            Vector3f localPos2 = element.position.sub(outliner.origin, new Vector3f());

                    var locatorTransform = new Node.Transform(localPos2.div(16), createQuaternion(locator.rotation), 1);
                    locatorTransform.mul(node.transform());

                    Node locatorNode = new Node(Node.NodeType.LOCATOR, node, locatorTransform, locator.name, locator.uuid, null, false);
                    nodeMap.put(locator.uuid, locatorNode);
                }

    protected void processTextDisplays(Object2ObjectOpenHashMap<UUID, Node> nodeMap, BbOutliner outliner, Node node) {
        List<BbElement> locatorElements = BbModelUtils.elementsForOutliner(model, outliner, BbElement.ElementType.TEXT_DISPLAY);
        for (BbElement element : locatorElements) {
            Vector3f localPos2 = element.position.sub(outliner.origin, new Vector3f());

            var locatorTransform = new Node.Transform(localPos2.div(16), createQuaternion(element.rotation), 1);
            locatorTransform.mul(node.transform());

            Node locatorNode = new Node(Node.NodeType.TEXT, node, locatorTransform, element.name, element.uuid, null, false, element);
            nodeMap.put(element.uuid, locatorNode);
        }
    }

    protected void processBlockDisplays(Object2ObjectOpenHashMap<UUID, Node> nodeMap, BbOutliner outliner, Node node) {
        List<BbElement> locatorElements = BbModelUtils.elementsForOutliner(model, outliner, BbElement.ElementType.BLOCK_DISPLAY);
        for (BbElement element : locatorElements) {
            Vector3f localPos2 = element.position.sub(outliner.origin, new Vector3f());

            var locatorTransform = new Node.Transform(localPos2.div(16), createQuaternion(element.rotation), 1);
            locatorTransform.mul(node.transform());

            Node locatorNode = new Node(Node.NodeType.BLOCK, node, locatorTransform, element.name, element.uuid, null, false, element);
            nodeMap.put(element.uuid, locatorNode);
        }
    }

    protected void processItemDisplays(Object2ObjectOpenHashMap<UUID, Node> nodeMap, BbOutliner outliner, Node node) {
        List<BbElement> locatorElements = BbModelUtils.elementsForOutliner(model, outliner, BbElement.ElementType.ITEM_DISPLAY);
        for (BbElement element : locatorElements) {
            Vector3f localPos2 = element.position.sub(outliner.origin, new Vector3f());

            var locatorTransform = new Node.Transform(localPos2.div(16), createQuaternion(element.rotation), 1);
            locatorTransform.mul(node.transform());

            Node locatorNode = new Node(Node.NodeType.ITEM, node, locatorTransform, element.name, element.uuid, null, false, element);
            nodeMap.put(element.uuid, locatorNode);
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

        for (var entry : nodeMap.entrySet()) {
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
            nodePath.addFirst(child);
            child = child.parent();
        }
        return nodePath;
    }

    @NotNull
    protected Reference2ObjectOpenHashMap<UUID, Pose> poses(BbAnimation animation, Object2ObjectOpenHashMap<UUID, Node> nodeMap, MolangEnvironment environment, float time) throws MolangRuntimeException {
        Reference2ObjectOpenHashMap<UUID, Pose> poses = new Reference2ObjectOpenHashMap<>();
        for (var entry : nodeMap.entrySet()) {
            Matrix4f matrix4f = new Matrix4f().rotateY(Mth.PI);
            //boolean requiresFrame = time == 0;
            List<Node> nodePath = nodePath(entry.getValue());

            for (var node : nodePath) {
                BbAnimator animator = animation.animators != null ? animation.animators.get(node.uuid()) : null;
                //requiresFrame |= animator != null;

                Vector3fc origin = node.transform().origin();

                var triple = animator == null ?
                        Triple.of(new Vector3f(), new Vector3f(), new Vector3f(1.f)) :
                        Sampler.sample(animator.keyframes, model.animationVariablePlaceholders, environment, time);

                Quaternionf localRot = createQuaternion(triple.getMiddle().mul(-1, -1, 1)).mul(node.transform().rotation());
                Vector3f localPos = triple.getLeft().mul(-1, 1, 1).div(16).add(origin);

                matrix4f.translate(localPos);
                matrix4f.rotate(localRot);
                matrix4f.scale(triple.getRight());
            }

            // TODO: check if frame is required?
            poses.put(entry.getKey(), Pose.of(matrix4f.scale(entry.getValue().transform().scale())));
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
        if (effectsUUID != null && anim.animators != null && anim.animators.containsKey(effectsUUID) && anim.animators.get(effectsUUID).type == BbAnimator.Type.effect) {
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
    protected Frame.Particle frameParticle(BbAnimation anim, float t) {
        UUID effectsUUID = CachedUuidDeserializer.get("effects");
        if (effectsUUID != null && anim.animators != null && anim.animators.containsKey(effectsUUID) && anim.animators.get(effectsUUID).type == BbAnimator.Type.effect) {
            BbAnimator animator = anim.animators.get(effectsUUID);
            for (BbKeyframe kf : animator.keyframes) {
                float difference = Mth.ceil(kf.time / 0.05f) * 0.05f; // snap value to 50ms increments
                if (difference == t && kf.channel == BbKeyframe.Channel.timeline) {
                    String key = "script";
                    var map = kf.dataPoints.getFirst();
                    String script = map.get(key).getStringValue();
                    if (!script.isEmpty()) {
                        var cmds = CommandParser.parse(map.get(key).getStringValue());
                        return new Frame.Particle(map.get("effect").getStringValue(), map.get("locator").getStringValue(), cmds, map.get("effect").getStringValue());
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    protected SoundEvent frameSound(BbAnimation anim, float t) {
        UUID effectsUUID = CachedUuidDeserializer.get("effects");
        if (effectsUUID != null && anim.animators != null && anim.animators.containsKey(effectsUUID) && anim.animators.get(effectsUUID).type == BbAnimator.Type.effect) {
            BbAnimator animator = anim.animators.get(effectsUUID);
            for (BbKeyframe kf : animator.keyframes) {
                float difference = Mth.ceil(kf.time / 0.05f) * 0.05f; // snap value to 50ms increments
                if (difference == t && kf.channel == BbKeyframe.Channel.sound) {
                    return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(kf.dataPoints.get(0).get("effect").getStringValue()));
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

        if (this.model.animations != null) this.model.animations.forEach(anim -> {
            try {
                int frameCount = Math.round(anim.length / step) + 1;
                Frame[] frames = new Frame[frameCount];

                for (int i = 0; i < frameCount; i++) {
                    float time = i * step;

                    MolangEnvironment env = MolangRuntime.runtime()
                            .setQuery("life_time", time)
                            .setQuery("anim_time", time)
                            .create();

                    // pose for bone in list of frames for an animation
                    Reference2ObjectOpenHashMap<UUID, Pose> poses = poses(anim, nodeMap, env, time);
                    frames[i] = new Frame(time, poses, this.frameVariant(anim, time), this.frameCommands(anim, time), this.frameSound(anim, time), this.frameParticle(anim, time));
                }

                // todo: cleanup!
                int startDelay = (int) (NumberUtils.isParsable(anim.startDelay) ? NumberUtils.createFloat(anim.startDelay) : 0);
                int loopDelay = (int) (NumberUtils.isParsable(anim.loopDelay) ? NumberUtils.createFloat(anim.loopDelay) : 0);

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
        ObjectArrayList<BbTexture> textures = model.textures;
        for (int i = 0, texturesSize = textures.size(); i < texturesSize; i++) {
            BbTexture texture = textures.get(i);
            textureMap.put(i, texture); // gen default textureMap
        }
        return textureMap;
    }

    @NotNull
    public Vec2 size() {
        // TODO: read from element or outliner
        return new Vec2(0.5f, 1.f);
    }

    @Override
    public Model importModel() {
        var nodeMap = this.makeNodeMap();
        var defaultPose = this.defaultPose(nodeMap);
        var animations = this.animations(nodeMap);
        var variants = this.variants();

        return new Model(nodeMap, defaultPose, variants, animations, this.size());
    }
}
