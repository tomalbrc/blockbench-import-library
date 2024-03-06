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

    private Pose toPose(Matrix4f matrix4f) {
        Matrix3f matrix3f = new Matrix3f(matrix4f);
        Vector3f translation = matrix4f.getTranslation(new Vector3f());

        float multiplier = 1.0F / matrix4f.m33();
        if (multiplier != 1.0F) {
            matrix3f.scale(multiplier);
            translation.mul(multiplier);
        }

        var triple = MatrixUtil.svdDecompose(matrix3f);
        Vector3f scale = triple.getMiddle();
        Quaternionf leftRotation = triple.getLeft();
        Quaternionf rightRotation = triple.getRight();
        return new Pose(UUID.randomUUID(), translation, scale, leftRotation, rightRotation);
    }

    private Reference2ObjectOpenHashMap<UUID, Pose> defaultPose(BbModel model) {
        Reference2ObjectOpenHashMap<UUID, Pose> res = new Reference2ObjectOpenHashMap<>();

        var list = model.modelOutliner();
        for (Outliner outliner: list) {
            Matrix4f matrix4f = new Matrix4f();
            Outliner xp = model.getParent(outliner);

            List<Outliner> nodePath = new ObjectArrayList<>();
            Outliner parent = outliner;
            while (parent != null) {
                nodePath.add(0, parent);
                parent = model.getParent(parent);
            }

            Vector3f prev = null;
            for (Outliner node: nodePath) {
                if (prev == null) {
                    var p = node.origin.mul(1 / 16.f, new Vector3f()).rotateY(Mth.PI);
                    matrix4f.translate(p);
                } else {
                    // relative position to parent bone, for correct rotation in default pose
                    Vector3f relativePos = node.origin.mul(1 / 16.f, new Vector3f()).rotateY(Mth.PI).sub(prev);
                    matrix4f.translate(relativePos);
                }

                if (node.rotation != null)
                    matrix4f.rotateXYZ(node.rotation.mul(Mth.DEG_TO_RAD, new Vector3f()).rotateY(Mth.PI));

                prev = node.origin.mul(1 / 16.f, new Vector3f()).rotateY(Mth.PI);
            }

            // Animated-java compat for larger models
            matrix4f.scale(outliner.scale);

            res.put(outliner.uuid, this.toPose(matrix4f));
        }

        return res;
    }

    private Reference2ObjectOpenHashMap<UUID, Variant> variants(BbModel model) {
        Reference2ObjectOpenHashMap<UUID, Variant> res = new Reference2ObjectOpenHashMap<>();
        return res;
    }

    private Object2ObjectOpenHashMap<String, Animation> animations(BbModel model) {
        Object2ObjectOpenHashMap<String, Animation> res = new Object2ObjectOpenHashMap<>();

        for (de.tomalbrc.bil.file.bbmodel.Animation anim: model.animations) {
            List<Frame> frames = new ObjectArrayList<>();
            float step = 0.05f;
            int frameCount = Math.round(anim.length / step);
            for (int i = 0; i <= frameCount; i++) {
                Reference2ObjectOpenHashMap<UUID, Pose> poses = new Reference2ObjectOpenHashMap<>();

                for (var nodeAnimatorEntry: anim.animators.entrySet()) {
                    Outliner bone = model.getOutliner(nodeAnimatorEntry.getKey());

                    if (bone != null) {
                        Matrix4f matrix4f = Sampler.sample(bone, nodeAnimatorEntry.getValue().keyframes, model.animationVariablePlaceholders, i * step);
                        if (matrix4f != null) {
                            poses.put(nodeAnimatorEntry.getKey(), this.toPose(matrix4f));
                        }
                    }
                }

                frames.add(new Frame(step * i, poses, null, null, null, false));
            }

            int startDelay = 0;
            int loopDelay = 0;
            int duration = Math.round(anim.length / step);
            Animation animation = new Animation(frames.toArray(new Frame[frames.size()]), startDelay, loopDelay, duration, anim.loop, new ReferenceOpenHashSet<>(), false);

            res.put(anim.name, animation);
        }

        return res;
    }

    private Vec2 size(BbModel model) {
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
