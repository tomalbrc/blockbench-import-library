package de.tomalbrc.bil.file.bbmodel;

import com.google.gson.annotations.SerializedName;
import de.tomalbrc.bil.BIL;
import de.tomalbrc.bil.file.extra.BbVariablePlaceholders;
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
    public List<BbOutliner.ChildEntry> outliner;
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

    public List<BbOutliner> modelOutliner() {
        List<BbOutliner> list = new ObjectArrayList<>();
        for (BbOutliner.ChildEntry entry: this.outliner) {
            if (entry.isNode()) {
                if (entry.outliner.hasModel() && !entry.outliner.isHitbox()) {
                    list.add(entry.outliner);
                }
                findModelOutlinerChildren(list, entry.outliner);
            }
        }
        return list;
    }

    private void findModelOutlinerChildren(List<BbOutliner> list, BbOutliner x) {
        for (BbOutliner.ChildEntry child: x.children) {
            if (child.isNode()) {
                if (child.outliner.hasModel() && !child.outliner.isHitbox())
                    list.add(child.outliner);

                findModelOutlinerChildren(list, child.outliner);
            }
        }
    }

    public BbOutliner getParent(BbElement element) {
        for (BbOutliner.ChildEntry entry: this.outliner) {
            if (entry.isNode()) {
                BbOutliner res = findParent(entry.outliner, element);
                if (res != null)
                    return res;
            }
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
}
