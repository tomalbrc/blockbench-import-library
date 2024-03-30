package de.tomalbrc.bil.file.extra;

import de.tomalbrc.bil.core.model.Node;
import de.tomalbrc.bil.file.bbmodel.BbElement;
import de.tomalbrc.bil.file.bbmodel.BbModel;
import de.tomalbrc.bil.file.bbmodel.BbOutliner;
import de.tomalbrc.bil.file.bbmodel.BbTexture;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.UUID;

public class BbModelUtils {
    public static BbElement getElement(BbModel model, UUID uuid) {
        for (BbElement element: model.elements) {
            if (element.uuid == uuid)
                return element;
        }
        return null;
    }

    public static BbTexture getTexture(BbModel model, UUID uuid) {
        for (BbTexture element: model.textures) {
            if (element.uuid == uuid)
                return element;
        }
        return null;
    }

    public static List<BbOutliner> modelOutliner(BbModel model) {
        List<BbOutliner> list = new ObjectArrayList<>();
        for (BbOutliner.ChildEntry entry: model.outliner) {
            if (entry.isNode()) {
                if (entry.outliner.hasModel() && !entry.outliner.isHitbox()) {
                    list.add(entry.outliner);
                }
                findModelOutlinerChildren(model, list, entry.outliner);
            }
        }
        return list;
    }

    public static void findModelOutlinerChildren(BbModel model, List<BbOutliner> list, BbOutliner x) {
        for (BbOutliner.ChildEntry child: x.children) {
            if (child.isNode()) {
                if (child.outliner.hasModel() && !child.outliner.isHitbox())
                    list.add(child.outliner);

                findModelOutlinerChildren(model, list, child.outliner);
            }
        }
    }

    public static BbOutliner getParent(BbModel model, BbElement element) {
        for (BbOutliner.ChildEntry entry: model.outliner) {
            if (entry.isNode()) {
                BbOutliner res = findParent(model, entry.outliner, element);
                if (res != null)
                    return res;
            }
        }
        return null;
    }

    public static BbOutliner findParent(BbModel model, BbOutliner x, BbElement element) {
        if (x.hasUuidChild(element.uuid))
            return x;

        for (BbOutliner.ChildEntry child: x.children) {
            if (child.isNode() && child.outliner.hasUuidChild(element.uuid)) {
                return child.outliner;
            } else if (child.isNode()) {
                var res = findParent(model, child.outliner, element);
                if (res != null)
                    return res;
            }
        }
        return null;
    }

    public static List<BbElement> elementsForOutliner(BbModel model, BbOutliner outliner) {
        List<BbElement> elements = new ObjectArrayList<>();
        for (BbElement element: model.elements) {
            if (outliner.hasUuidChild(element.uuid) && element.type == BbElement.ElementType.CUBE) {
                elements.add(element);
            }
        }
        return elements;
    }
}
