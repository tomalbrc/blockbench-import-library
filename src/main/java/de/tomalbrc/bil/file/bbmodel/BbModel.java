package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.joml.Vector2i;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BbModel {
    public BbMeta meta;
    public String name;
    @SerializedName("model_identifier")
    public String modelIdentifier;

    @SerializedName("unhandled_root_fields")
    public Map<String, Object> unhandledRootFields;
    public Vector2i resolution;
    public List<BbElement> elements;
    public List<BbOutliner> outliner;
    public List<BbTexture> textures;
    public List<BbAnimation> animations;

    @SerializedName("animation_variable_placeholders")
    public BbVariablePlaceholders animationVariablePlaceholders;


    public BbElement getElement(UUID uuid) {
        for (BbElement element: this.elements) {
            if (element.uuid.equals(uuid))
                return element;
        }
        return null;
    }

    public BbOutliner getOutliner(UUID uuid) {
        for (BbOutliner outliner: this.outliner) {
            BbOutliner res = findNode(outliner, uuid);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    public List<BbOutliner> modelOutliner() {
        List<BbOutliner> list = new ObjectArrayList<>();

        for (BbOutliner outliner: this.outliner) {
            if (outliner.hasModel()) {
                list.add(outliner);
            }

            findModelOutlinerChildren(list, outliner);
        }

        return list;
    }

    private void findModelOutlinerChildren(List<BbOutliner> list, BbOutliner x) {
        for (BbOutliner.ChildEntry child: x.children) {
            if (child.isNode()) {
                if (child.outliner.hasModel())
                    list.add(child.outliner);

                findModelOutlinerChildren(list, child.outliner);
            }
        }
    }


    private BbOutliner findNode(BbOutliner x, UUID uuid) {
        if (x.uuid.equals(uuid)) {
            return x;
        }
        for (BbOutliner.ChildEntry child: x.children) {
            if (child.isNode() && child.outliner.uuid.equals(uuid)) {
                return child.outliner;
            } else if (child.isNode()) {
                var res = findNode(child.outliner, uuid);
                if (res != null)
                    return res;
            }
        }
        return null;
    }

    public BbOutliner getParent(BbElement element) {
        for (BbOutliner outliner: this.outliner) {
            BbOutliner res = findParent(outliner, element);
            if (res != null)
                return res;
        }
        return null;
    }

    private BbOutliner findParent(BbOutliner x, BbElement element) {
        if (x.hasUuidChild(element.uuid))
            return x;

        for (BbOutliner.ChildEntry child: x.children) {
            if (child.isNode() && child.outliner.hasUuidChild(element.uuid)) {
                return child.outliner;
            } else if (child.isNode()) {
                var res = findParent(child.outliner, element);
                if (res != null)
                    return res;
            }
        }
        return null;
    }

    public BbOutliner getParent(BbOutliner x) {
        for (BbOutliner outliner: this.outliner) {
            BbOutliner res = findParent(outliner, x);
            if (res != null)
                return res;
        }
        return null;
    }

    private BbOutliner findParent(BbOutliner x, BbOutliner childOutliner) {
        if (x.hasChildOutliner(childOutliner))
            return x;

        for (BbOutliner.ChildEntry child: x.children) {
            if (child.isNode() && child.outliner.hasChildOutliner(childOutliner)) {
                return child.outliner;
            } else if (child.isNode()) {
                var res = findParent(child.outliner, childOutliner);
                if (res != null)
                    return res;
            }
        }
        return null;
    }
}
