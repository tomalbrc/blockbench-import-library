package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.bil.core.model.Frame;
import de.tomalbrc.bil.core.model.Pose;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BbModel {
    public Meta meta;
    public String name;
    @SerializedName("model_identifier")
    public String modelIdentifier;

    @SerializedName("unhandled_root_fields")
    public Map<String, Object> unhandledRootFields;
    public Vector2i resolution;
    public List<Element> elements;
    public List<Outliner> outliner;
    public List<Texture> textures;
    public List<Animation> animations;

    @SerializedName("animation_variable_placeholders")
    public VariablePlaceholders animationVariablePlaceholders;


    public Element getElement(UUID uuid) {
        for (Element element: this.elements) {
            if (element.uuid.equals(uuid))
                return element;
        }
        return null;
    }

    public Outliner getOutliner(UUID uuid) {
        for (Outliner outliner: this.outliner) {
            Outliner res = findNode(outliner, uuid);
            if (res != null) {
                return res;
            }
        }
        return null;
    }

    public List<Outliner> modelOutliner() {
        List<Outliner> list = new ObjectArrayList<>();

        for (Outliner outliner: this.outliner) {
            if (outliner.hasModel()) {
                list.add(outliner);
            }

            findModelOutlinerChildren(list, outliner);
        }

        return list;
    }

    private void findModelOutlinerChildren(List<Outliner> list, Outliner x) {
        for (Outliner.ChildEntry child: x.children) {
            if (child.isNode()) {
                if (child.outliner.hasModel())
                    list.add(child.outliner);

                findModelOutlinerChildren(list, child.outliner);
            }
        }
    }


    private Outliner findNode(Outliner x, UUID uuid) {
        if (x.uuid.equals(uuid)) {
            return x;
        }
        for (Outliner.ChildEntry child: x.children) {
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

    public Outliner getParent(Element element) {
        for (Outliner outliner: this.outliner) {
            Outliner res = findParent(outliner, element);
            if (res != null)
                return res;
        }
        return null;
    }

    private Outliner findParent(Outliner x, Element element) {
        if (x.hasUuidChild(element.uuid))
            return x;

        for (Outliner.ChildEntry child: x.children) {
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

    public Outliner getParent(Outliner x) {
        for (Outliner outliner: this.outliner) {
            Outliner res = findParent(outliner, x);
            if (res != null)
                return res;
        }
        return null;
    }

    private Outliner findParent(Outliner x, Outliner childOutliner) {
        if (x.hasChildOutliner(childOutliner))
            return x;

        for (Outliner.ChildEntry child: x.children) {
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
