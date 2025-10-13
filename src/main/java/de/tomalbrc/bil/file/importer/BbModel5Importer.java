package de.tomalbrc.bil.file.importer;

import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.file.bbmodel.BbElement;
import de.tomalbrc.bil.file.bbmodel.BbGroup;
import de.tomalbrc.bil.file.bbmodel.BbModel;
import de.tomalbrc.bil.file.bbmodel.BbOutliner;
import de.tomalbrc.bil.file.extra.BbModelUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
            if (element.type != BbElement.ElementType.CUBE_MODEL) continue;

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
                ResourceLocation modelPath = null;

                BbGroup group = BbModelUtils.getGroup(model, entry);
                if (group == null)
                    continue;

                if (outliner.hasModel() && group.export && !outliner.isHitbox()) {
                    modelPath = this.generateModel(outliner);
                }

                Vector3f localPos = parentGroup != null ? group.origin.sub(parentGroup.origin, new Vector3f()) : new Vector3f(group.origin);
                Quaternionf localRot = createQuaternion(group.rotation);

                var tr = new Node.Transform(localPos.div(16), localRot, group.scale);
                if (parentOutliner != null)
                    tr.mul(parent.transform());
                else
                    tr.mul(new Matrix4f().rotateY(Mth.PI));

                Node node = new Node(Node.NodeType.BONE, parent, tr, group.name, outliner.uuid, modelPath, group.name.startsWith("head"), null);
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

            Node locatorNode = new Node(Node.NodeType.LOCATOR, node, locatorTransform, element.name, element.uuid, null, false, null);
            nodeMap.put(element.uuid, locatorNode);
        }
    }

    protected void processTextDisplays(Object2ObjectOpenHashMap<UUID, Node> nodeMap, BbOutliner outliner, Node node) {
        List<BbElement> locatorElements = BbModelUtils.elementsForOutliner(model, outliner, BbElement.ElementType.TEXT_DISPLAY);
        for (BbElement element : locatorElements) {
            BbGroup group = BbModelUtils.getGroup(model, outliner);
            if (group == null)
                continue;

            Vector3f localPos2 = element.position.sub(group.origin, new Vector3f());

            var locatorTransform = new Node.Transform(localPos2.div(16), createQuaternion(element.rotation), 1);
            locatorTransform.mul(node.transform());

            Node locatorNode = new Node(Node.NodeType.TEXT, node, locatorTransform, element.name, element.uuid, null, false, element);
            nodeMap.put(element.uuid, locatorNode);
        }
    }

    protected void processBlockDisplays(Object2ObjectOpenHashMap<UUID, Node> nodeMap, BbOutliner outliner, Node node) {
        List<BbElement> locatorElements = BbModelUtils.elementsForOutliner(model, outliner, BbElement.ElementType.BLOCK_DISPLAY);
        for (BbElement element : locatorElements) {
            BbGroup group = BbModelUtils.getGroup(model, outliner);
            if (group == null)
                continue;

            Vector3f localPos2 = element.position.sub(group.origin, new Vector3f());

            var locatorTransform = new Node.Transform(localPos2.div(16), createQuaternion(element.rotation), 1);
            locatorTransform.mul(node.transform());

            Node locatorNode = new Node(Node.NodeType.BLOCK, node, locatorTransform, element.name, element.uuid, null, false, element);
            nodeMap.put(element.uuid, locatorNode);
        }
    }

    protected void processItemDisplays(Object2ObjectOpenHashMap<UUID, Node> nodeMap, BbOutliner outliner, Node node) {
        List<BbElement> locatorElements = BbModelUtils.elementsForOutliner(model, outliner, BbElement.ElementType.ITEM_DISPLAY);
        for (BbElement element : locatorElements) {
            BbGroup group = BbModelUtils.getGroup(model, outliner);
            if (group == null)
                continue;

            Vector3f localPos2 = element.position.sub(group.origin, new Vector3f());

            var locatorTransform = new Node.Transform(localPos2.div(16), createQuaternion(element.rotation), 1);
            locatorTransform.mul(node.transform());

            Node locatorNode = new Node(Node.NodeType.ITEM, node, locatorTransform, element.name, element.uuid, null, false, element);
            nodeMap.put(element.uuid, locatorNode);
        }
    }
}
