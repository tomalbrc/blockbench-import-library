package de.tomalbrc.bil.file.importer;

import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.core.model.Pose;
import de.tomalbrc.bil.file.bbmodel.*;
import de.tomalbrc.bil.file.extra.BbModelUtils;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

// bbmodel for 5.x format
public class BbModel5Importer extends BbModelImporter {

    public BbModel5Importer(BbModel model) {
        super(model);
    }

    protected void postProcess(BbModel model) {
        for (BbElement element : model.elements) {
            if (element.type != BbElement.ElementType.CUBE) continue;

            // remove elements without texture
            element.faces.entrySet().removeIf(entry -> entry.getValue().texture == null);

            this.rescaleUV(model.resolution, model.textures, element);
            this.inflateElement(element);

            BbOutliner parent = BbModelUtils.getParent(model, element);
            if (parent != null) {
                BbGroup group = BbModelUtils.getGroup(model, parent);
                element.from.sub(group.origin);
                element.to.sub(group.origin);
            }
        }

        for (BbOutliner parent : BbModelUtils.modelOutliner(model)) {
            Vector3f min = new Vector3f(), max = new Vector3f();
            // find max for scale (aj compatibility)
            for (var childEntry : parent.children) {
                if (!childEntry.isNode()) {
                    BbElement element = BbModelUtils.getElement(model, childEntry.uuid);
                    if (element != null && element.type == BbElement.ElementType.CUBE) {
                        min.min(element.from);
                        max.max(element.to);
                    }
                }
            }

            for (var childEntry : parent.children) {
                if (!childEntry.isNode()) {
                    BbElement element = BbModelUtils.getElement(model, childEntry.uuid);
                    if (element == null || element.type != BbElement.ElementType.CUBE) continue;

                    var diff = min.sub(max, new Vector3f()).absolute();
                    float m = diff.get(diff.maxComponent());
                    float scale = Math.min(1.f, 24.f / m);

                    // for animation + default pose later, to allow for larger models
                    BbGroup group = BbModelUtils.getGroup(model, parent);
                    group.scale = 1.f / scale;

                    element.from.mul(scale).add(8, 8, 8);
                    element.to.mul(scale).add(8, 8, 8);

                    element.origin.sub(group.origin).mul(scale).add(8, 8, 8);
                }
            }
        }
    }

    @Override
    protected void createBones(Node parent, BbOutliner parentOutliner, Collection<BbOutliner.ChildEntry> children, Object2ObjectOpenHashMap<UUID, Node> nodeMap) {
        BbGroup parentGroup = BbModelUtils.getGroup(model, parentOutliner);

        for (BbOutliner.ChildEntry entry : children) {
            if (entry.isNode()) {
                BbOutliner outliner = entry.outliner;
                PolymerModelData modelData = null;

                BbGroup group = BbModelUtils.getGroup(model, entry.outliner);
                if (group == null)
                    continue;

                if (outliner.hasModel() && group.export && !outliner.isHitbox()) {
                    modelData = this.generateModel(outliner);
                }

                Vector3f localPos = parentGroup != null ? group.origin.sub(parentGroup.origin, new Vector3f()) : new Vector3f(group.origin);
                Quaternionf localRot = createQuaternion(group.rotation);

                var tr = new Node.Transform(localPos.div(16), localRot, group.scale);
                if (parentOutliner != null)
                    tr.mul(parent.transform());
                else
                    tr.mul(new Matrix4f().rotateY(Mth.PI));

                Node node = new Node(Node.NodeType.BONE, parent, tr, group.name, outliner.uuid, modelData, group.name.startsWith("head"));
                nodeMap.put(outliner.uuid, node);

                processLocators(nodeMap, outliner, node);

                // children
                createBones(node, outliner, outliner.children, nodeMap);
            }
        }
    }

    @Override
    protected void processLocators(Object2ObjectOpenHashMap<UUID, Node> nodeMap, BbOutliner outliner, Node node) {
        List<BbElement> locatorElements = BbModelUtils.elementsForOutliner(model, outliner, BbElement.ElementType.LOCATOR);
        for (BbElement element : locatorElements) {
            BbGroup group = BbModelUtils.getGroup(model, outliner);
            if (group == null)
                continue;

            Vector3f localPos2 = element.position.sub(group.origin, new Vector3f());

            var locatorTransform = new Node.Transform(localPos2.div(16), createQuaternion(element.rotation), 1);
            locatorTransform.mul(node.transform());

            Node locatorNode = new Node(Node.NodeType.LOCATOR, node, locatorTransform, element.name, element.uuid, null, false);
            nodeMap.put(element.uuid, locatorNode);
        }
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

                Quaternionf localRot = createQuaternion(triple.getMiddle()).mul(node.transform().rotation());
                Vector3f localPos = triple.getLeft().div(16).add(origin);

                matrix4f.translate(localPos);
                matrix4f.rotate(localRot);
                matrix4f.scale(triple.getRight());
            }

            // TODO: check if frame is required?
            poses.put(entry.getKey(), Pose.of(matrix4f.scale(entry.getValue().transform().scale())));
        }
        return poses;
    }
}
