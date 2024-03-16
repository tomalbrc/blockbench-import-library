package de.tomalbrc.bil.file.importer;

import de.tomalbrc.bil.file.extra.BbResourcePackGenerator;
import de.tomalbrc.bil.file.bbmodel.*;
import de.tomalbrc.bil.core.model.*;
import de.tomalbrc.bil.core.model.Animation;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec2;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.*;

import java.lang.Math;
import java.util.List;
import java.util.UUID;

public class BbModelImporter implements ModelImporter<BbModel> {
    private Object2ObjectOpenHashMap<UUID, Node> nodeMap(BbModel model) {
        Object2ObjectOpenHashMap<UUID, Node> nodeMap = new Object2ObjectOpenHashMap<>();
        ObjectArraySet<BbTexture> textures = new ObjectArraySet<>();

        for (BbOutliner outliner: model.modelOutliner()) {
            if (outliner.export) {
                List<BbElement> elements = new ObjectArrayList<>();
                for (BbElement element: model.elements) {
                    if (outliner.hasUuidChild(element.uuid)) {
                        elements.add(element);
                    }
                }

                String modelName = outliner.name;

                ResourceLocation location = BbResourcePackGenerator.makePart(model, modelName, elements, model.textures);
                textures.addAll(model.textures);

                RPModelInfo modelInfo = new RPModelInfo(PolymerResourcePackUtils.requestModel(Items.PAPER, location).value(), location);
                nodeMap.put(outliner.uuid, new Node(Node.NodeType.bone, outliner.name, outliner.uuid, modelInfo));
            }
        }

        BbResourcePackGenerator.makeTextures(model, textures);

        return nodeMap;
    }

    private Quaternionf createQuaternion(Vector3f eulerAngles) {
        return new Quaternionf()
                .rotateZ(Mth.DEG_TO_RAD * eulerAngles.z)
                .rotateY(Mth.DEG_TO_RAD * eulerAngles.y)
                .rotateX(Mth.DEG_TO_RAD * eulerAngles.x);
    }

    private Reference2ObjectOpenHashMap<UUID, Pose> defaultPose(BbModel model) {
        Reference2ObjectOpenHashMap<UUID, Pose> res = new Reference2ObjectOpenHashMap<>();

        var list = model.modelOutliner();
        for (BbOutliner bone: list) {
            List<BbOutliner> nodePath = nodePath(model, bone);

            Matrix4f matrix4f = new Matrix4f().rotateY(Mth.PI);
            BbOutliner parent = null;
            for (BbOutliner node: nodePath) {
                var localPos = parent != null ? node.origin.sub(parent.origin, new Vector3f()) : new Vector3f(node.origin);

                matrix4f.translate(localPos.div(16));
                matrix4f.rotate(createQuaternion(node.rotation));

                parent = node;
            }
            res.put(bone.uuid, Pose.of(matrix4f.scale(bone.scale)));
        }

        return res;
    }

    private Reference2ObjectOpenHashMap<UUID, Variant> variants(BbModel model) {
        Reference2ObjectOpenHashMap<UUID, Variant> res = new Reference2ObjectOpenHashMap<>();
        return res;
    }

    private List<BbOutliner> nodePath(BbModel model, BbOutliner nestedOutliner) {
        List<BbOutliner> nodePath = new ObjectArrayList<>();
        BbOutliner parent = nestedOutliner;
        while (parent != null) {
            nodePath.add(0, parent);
            parent = model.getParent(parent);
        }
        return nodePath;
    }

    private Reference2ObjectOpenHashMap<UUID, Pose> poses(BbModel model, BbAnimation animation, float time) {
        Reference2ObjectOpenHashMap<UUID, Pose> poses = new Reference2ObjectOpenHashMap<>();

        for (BbOutliner bone: model.modelOutliner()) {
            List<BbOutliner> nodePath = nodePath(model, bone);

            // to check if any parent node has an animator, if not then there is no animation happening at all,
            // no need to save the frame
            boolean requiresFrame = false;

            BbOutliner parent = null;

            Matrix4f matrix4f = new Matrix4f().rotateY(Mth.PI);

            // sample from root to bone
            for (BbOutliner node: nodePath) {
                BbAnimator animator = animation.animators.get(node.uuid);
                requiresFrame |= animator != null;

                Vector3f origin = parent != null ? node.origin.sub(parent.origin, new Vector3f()) : new Vector3f(node.origin);

                var triple = animator == null ? Triple.of(new Vector3f(), new Vector3f(), new Vector3f(1.f)) : Sampler.sample(node, animator.keyframes, model.animationVariablePlaceholders, time);

                Vector3f localRot = node.rotation.add(triple.getMiddle().mul(-1,-1,1), new Vector3f());
                Vector3f localPos = origin.add(triple.getLeft());

                matrix4f.translate(localPos.div(16));
                matrix4f.rotate(createQuaternion(localRot));
                matrix4f.scale(triple.getRight());

                parent = node;
            }

            if (requiresFrame)
                poses.put(bone.uuid, Pose.of(matrix4f.scale(bone.scale)));
        }
        return poses;
    }

    private Object2ObjectOpenHashMap<String, Animation> animations(BbModel model) {
        Object2ObjectOpenHashMap<String, Animation> res = new Object2ObjectOpenHashMap<>();
        float step = 0.05f;
        for (BbAnimation anim: model.animations) {
            List<Frame> frames = new ObjectArrayList<>();
            int frameCount = Math.round(anim.length / step);
            for (int i = 0; i <= frameCount; i++) {
                float time = i * step;

                // pose for bone in list of frames for an animation
                Reference2ObjectOpenHashMap<UUID, Pose> poses = poses(model, anim, time);
                frames.add(new Frame(time, poses, null, null, null, false));
            }

            int startDelay = 0;
            int loopDelay = 0;
            Animation animation = new Animation(frames.toArray(new Frame[frames.size()]), startDelay, loopDelay, frameCount, anim.loop, new ReferenceOpenHashSet<>(), false);

            res.put(anim.name, animation);
        }

        return res;
    }

    private Vec2 size(BbModel model) {
        // TODO: read from element or outliner
        return new Vec2(0.5f,1.f);
    }

    @Override
    public Model importModel(BbModel model) {
        Object2ObjectOpenHashMap<UUID, Node> nodeMap = this.nodeMap(model);
        Reference2ObjectOpenHashMap<UUID, Pose> defaultPose = this.defaultPose(model);
        Reference2ObjectOpenHashMap<UUID, Variant> variants = this.variants(model);
        Object2ObjectOpenHashMap<String, Animation> animations = this.animations(model);

        Model result = new Model(nodeMap, defaultPose, variants, animations, this.size(model));

        return result;
    }
}
