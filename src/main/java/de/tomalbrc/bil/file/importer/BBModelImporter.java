package de.tomalbrc.bil.file.importer;

import com.mojang.math.Axis;
import com.mojang.math.MatrixUtil;
import de.tomalbrc.bil.datagen.RPDataGenerator;
import de.tomalbrc.bil.file.bbmodel.*;
import de.tomalbrc.bil.core.model.*;
import de.tomalbrc.bil.core.model.Animation;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec2;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.*;

import java.lang.Math;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BBModelImporter implements ModelImporter<BbModel> {

    private Object2ObjectOpenHashMap<UUID, Node> nodeMap(BbModel model) {
        Object2ObjectOpenHashMap<UUID, Node> nodeMap = new Object2ObjectOpenHashMap<>();
        List<Texture> textures = new ObjectArrayList<>();

        for (Outliner outliner: model.modelOutliner()) {
            if (outliner.export) {
                List<Element> elements = new ObjectArrayList<>();
                for (Element element: model.elements) {
                    if (outliner.hasUuidChild(element.uuid)) {
                        elements.add(element);
                    }
                }

                String modelName = outliner.name;

                RPDataGenerator.makePart(model, modelName, elements, model.textures);
                textures.addAll(model.textures);

                ResourceLocation location = RPDataGenerator.locationOf(model, modelName);
                nodeMap.put(outliner.uuid, new Node(Node.NodeType.bone, outliner.name, outliner.uuid, new RPModelInfo(PolymerResourcePackUtils.requestModel(Items.PAPER, location).value(), location)));
            }
        }

        RPDataGenerator.makeTextures(model, textures);

        return nodeMap;
    }

    private Reference2ObjectOpenHashMap<UUID, Pose> defaultPose(BbModel model) {
        Reference2ObjectOpenHashMap<UUID, Pose> res = new Reference2ObjectOpenHashMap<>();

        var list = model.modelOutliner();
        for (Outliner outliner: list) {
            Matrix4f matrix4f = new Matrix4f();

            List<Outliner> nodePath = new ObjectArrayList<>();
            Outliner parent = outliner;
            while (parent != null) {
                nodePath.add(0, parent);
                parent = model.getParent(parent);
            }

            Vector3f prev = null;
            for (Outliner node: nodePath) {
                if (prev == null) {
                    var p = node.origin.mul(1 / 16.f, new Vector3f());
                    matrix4f.translate(p);
                } else {
                    // relative position to parent bone, for correct rotation in default pose
                    Vector3f relativePos = node.origin.mul(1 / 16.f, new Vector3f()).sub(prev);
                    matrix4f.translate(relativePos);
                }

                if (node.rotation != null)
                    matrix4f.rotateXYZ(node.rotation.mul(Mth.DEG_TO_RAD, new Vector3f()));

                prev = node.origin.mul(1 / 16.f, new Vector3f());
            }

            // Animated-java compat for larger models
            matrix4f.scale(outliner.scale);

            res.put(outliner.uuid, Pose.of(matrix4f));
        }

        return res;
    }

    private Reference2ObjectOpenHashMap<UUID, Variant> variants(BbModel model) {
        Reference2ObjectOpenHashMap<UUID, Variant> res = new Reference2ObjectOpenHashMap<>();
        return res;
    }

    private Object2ObjectOpenHashMap<String, Animation> animations(BbModel model) {
        Object2ObjectOpenHashMap<String, Animation> res = new Object2ObjectOpenHashMap<>();

        float step = 0.05f;

        for (de.tomalbrc.bil.file.bbmodel.Animation anim: model.animations) {
            List<Frame> frames = new ObjectArrayList<>();
            int frameCount = Math.round(anim.length / step);
            for (int i = 0; i <= frameCount; i++) {
                // pose for bone in list of frames for an animation
                Reference2ObjectOpenHashMap<UUID, Pose> poses = new Reference2ObjectOpenHashMap<>();

                for (Outliner bone: model.modelOutliner()) {
                    List<Outliner> nodePath = new ObjectArrayList<>();
                    Outliner parent = bone;
                    while (parent != null) {
                        nodePath.add(0, parent);
                        parent = model.getParent(parent);
                    }

                    parent = null;

                    Vector3f parentPos = new Vector3f();
                    Quaternionf parentRot = new Quaternionf();
                    Vector3f parentScale = new Vector3f(1.f/16.f);
                    // sample from root to bone
                    boolean requiresFrame = false;
                    for (var node: nodePath) {
                        Animator a = anim.animators.get(node.uuid);
                        requiresFrame |= a != null;

                        Vector3f origin;
                        if (parent != null)
                            origin = node.origin.sub(parent.origin, new Vector3f());
                        else {
                            origin = new Vector3f(node.origin);
                        }

                        var triple = a == null ? Triple.of(new Vector3f(), new Vector3f(), new Vector3f(1.f)) : Sampler.sample(node, a.keyframes, model.animationVariablePlaceholders, i * step);
                        var localRot = node.rotation.add(triple.getMiddle(), new Vector3f()).mul(Mth.DEG_TO_RAD);
                        var localPos = origin.add(triple.getLeft());

                        parentScale.mul(triple.getRight().mul(localRot));
                        parentPos = localPos.mul(1/16.f).rotate(parentRot).add(parentPos);
                        parentRot.mul(new Quaternionf().rotateXYZ(-localRot.x, -localRot.y, localRot.z));

                        parent = node;
                    }

                    if (requiresFrame)
                        poses.put(bone.uuid, Pose.of(new Matrix4f().rotateY(Mth.PI).translate(parentPos).rotate(parentRot).scale(parentScale)));
                }

                frames.add(new Frame(step * i, poses, null, null, null, false));
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

        Model result = new Model(this.size(model), nodeMap, defaultPose, variants, animations);

        return result;
    }
}
